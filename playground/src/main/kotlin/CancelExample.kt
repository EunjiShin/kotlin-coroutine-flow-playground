import kotlinx.coroutines.*

/*
* cancel()과 isActive를 이용해 특정 job을 명시적으로 취소할 수 있다.
* 코루틴에 할당된 자원이 강제 취소가 발생했을 때도 잘 회수되도록 예외처리가 필요하다.
* 취소 불가능한 블록을 만들고 싶다면 withContext(NonCancellable)를 이용하자.
* 일정 시간이 지난 후 루틴을 종료하고 싶다면 withTimeout을 이용하자.
* */

suspend fun doOneTwo() = coroutineScope {
    val job1 = launch {
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L)
        println("2!")
    }

    val job2 = launch {
        println("launch3: ${Thread.currentThread().name}")
        delay(500L)
        println("1!")
    }

    delay(800L)
    job1.cancel() // 호출 시점에 아직 안 끝났기에 강제로 취소됨
    job2.cancel() // 호출 시점에 이미 끝나있기에 cancel 영향 없음
    println("3!")
}

suspend fun doCount() = coroutineScope {
    // launch(Dispatchers.Default)는 코드 블럭을 다른 스레드에서 수행시킨다.
    val job1 = launch(Dispatchers.Default) {
        var i = 1
        var nextTime = System.currentTimeMillis() + 100L

        // isActive를 이용해 해당 코루틴이 활성화되었는지, 종료되었는지 확인할 수 있다
        // cancel되는 순간 isActive가 false로 변환되어 반복 탈출!
        while (i <= 10 && isActive) {
            val currentTime = System.currentTimeMillis()
            if (currentTime >= nextTime) {
                println()
                nextTime = currentTime + 100L
                i++
            }
        }
    }

    delay(200L)   // 200ms 기다린 후
    job1.cancelAndJoin()   // cancel 이후 join을 해주는 코드
    println("doCount Done!")
}

// suspend function은 JobCancellationException을 발생한다 -> try-catch-finally로 대응할 수 있다.
// 외부에 의해 코루틴이 취소되어 cancel 과정이 따로 없더라도, 할당된 자원을 회수해야 한다는 것을 기억하자.
suspend fun doOneTwoWithFinally() = coroutineScope {
    val job1 = launch {
        try {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("2!")
        } finally {
            println("job1 is finishing!") // 로그 역할
            // 파일을 닫아주는 코드 or 소켓을 닫아주는 코드 -> 자원 회수
        }
    }

    // 만약 취소 불가능한 코드를 만들고 싶다면? -> withContext(NonCancellable)를 이용하자.
    val job2 = launch {
        withContext(NonCancellable) {
            println("launch2: ${Thread.currentThread().name}")
            delay(100L)
            println("1!")
        }
        delay(1000L)
        println("job2 : end")
    }

    delay(800L)
    job1.cancel()
    job2.cancel()
    println("3!")
}

fun main() = runBlocking {
    doOneTwo()
    println("runBlocking: ${Thread.currentThread().name}")

    // 일정 시간이 지나면 강제로 취소하고 싶을 때 사용한다. TimeoutCancellationException이 발생한다.
    withTimeout(500L) {
        doCount()
    }

    // timeout 발생할 때마다 예외를 핸들링하는게 번거롭다면 withTimeoutOrNull을 사용할 수 있다. 이 경우 타임아웃 시 null을 반환한다.
    // 이후 상황에 따라 엘비스 연산자를 이용해 적당한 값을 할당하면 된다.
    val result = withTimeoutOrNull(500L) {
        doCount()
        true
    } ?: false
}