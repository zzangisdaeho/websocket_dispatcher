package com.autocrypt.websocket_dispatcher.dispatch.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

/**
 * 김대호
 * 병렬 처리 중 먼저 끝나는 쓰레드의 값 중 유효한 값을 바로 반환
 * default finder : 유효한 값 = null이 아니면서 에러가 터지지 않아야함
 * 먼저 유효한 값이 있으면, 다른 작업이 끝나지 않더라도 바로 받아볼 수 있음.
 * @param <T> return future generic type
 */
@FunctionalInterface
public interface FirstValidFinder<T> {
    CompletableFuture<T> findFirstValid(List<CompletableFuture<T>> futures);

    Logger log = LoggerFactory.getLogger(FirstValidFinder.class);

    /**
     * 김대호
     * @return 첫번째 결과값이 포함된 completablefuture. 유효한 데이터가 없는 경우 에러 발생
     * @param <T>
     */
    static <T> FirstValidFinder<T> defaultFinder() {
        return futures -> {
            CompletableFuture<T> result = new CompletableFuture<>();

            List<CompletableFuture<Void>> wrappedFutures = futures.stream()
                    .map(future -> future.thenAccept(value -> {
                        log.debug("value arrive : {}", value);
                        if (value != null && !result.isDone()) {  // ✅ Null이 아니고, 아직 완료되지 않았다면 처리
                            log.info("✅ Valid result found: {}", value);
                            result.complete(value);
                        } else {
                            log.debug("⚠️ ignoring value: {}", value);
                        }
                    }).exceptionally(ex -> {
                        log.warn("⚠️ Future execution failed: {}", ex.getMessage(), ex);
                        return null; // 예외 발생 시 무시
                    }))
                    .toList(); // `CompletableFuture<Void>` 리스트 생성

            // 모든 Future가 실패 또는 Null이면 예외 발생
            CompletableFuture.allOf(wrappedFutures.toArray(new CompletableFuture[0]))
                    .thenRun(() -> {
                        if (!result.isDone()) {
                            // 실패한 모든 예외 메시지를 모아 Exception 던지기
                            String errorMsg = futures.stream()
                                    .map(f -> {
                                        try {
                                            T resultValue = f.get();
                                            return resultValue != null ? resultValue.toString() : "(null)";
                                        } catch (Exception e) {
                                            return "Exception: " + e.getMessage();
                                        }
                                    })
                                    .collect(Collectors.joining(", "));

                            result.completeExceptionally(
                                    new CompletionException("🚨 No valid results found. Errors: " + errorMsg, null)
                            );
                        }
                    });

            return result;
        };
    }

    /**
     * 김대호
     * @return 첫번째 결과값이 포함된 completable future. 데이터가 없는경우 null 반환
     * @param <T>
     */
    static <T> FirstValidFinder<T> defaultFinderNullable() {
        return futures -> {
            CompletableFuture<T> result = new CompletableFuture<>();

            // `thenAccept()`가 실행되는 Future 리스트 생성
            List<CompletableFuture<Void>> wrappedFutures = futures.stream()
                    .map(future -> future.thenAccept(value -> {
                        if (value != null && !result.isDone()) {
                            result.complete(value);
                        }
                    }).exceptionally(ex -> null)) // 예외 발생 시 무시
                    .toList();

            // 모든 Future가 실행 완료된 후 valid한 값이 없으면 null 반환
            CompletableFuture.allOf(wrappedFutures.toArray(new CompletableFuture[0]))
                    .thenRun(() -> {
                        if (!result.isDone()) {  // 모든 요청이 실패했을 때만 실행
                            result.complete(null);
                        }
                    });

            return result;
        };
    }
}