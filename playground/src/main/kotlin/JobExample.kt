import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/* 코루틴 빌더
runBlocking은 현재 스레드를 멈추고 기다리게 한다. (withContext도 동일)
coroutineScope는 현재 스레드를 멈추지 않는다.
*/

// 스코프 빌더를 이용해 코루틴 스코프를 만들 수 있다. suspend fun 내에서 스코프 빌더를 만들고 싶을 때 사용
// runBlocking의 자식 코루틴
suspend fun doOneTwoThree() = coroutineScope {
    // coroutineScope 내부가 코루틴 바디
    // this는 코루틴. 따라서 this.launch = launch
    launch {
        // launch는 coroutineScope의 자식 코루틴
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L)
        println("3!")
    }

    // launch 블럭은 job 객체를 반환한다.
    val job = launch {
        println("launch2: ${Thread.currentThread().name}")
        println("1!")
    }
    // join을 사용하면 내부 블럭 실행이 종료될 때까지 주도권을 점유한다. delay가 있더라도 다른 코루틴에 양보하지 않음!
    job.join() // suspension point

    launch {
        println("launch3: ${Thread.currentThread().name}")
        delay(500L)
        println("2!")
    }

    // repeat을 이용해 내부 블럭을 반복할 수 있다. 여기선 launch 블럭을 1000번 반복한다.
    repeat(1000) {
        launch {
            println("launch4: ${Thread.currentThread().name}")
            delay(500L)
            println("5!")
        }
    }
    println("4!")
}

// runBlocking이 부모 코루틴
fun main() = runBlocking {
    doOneTwoThree()
    println("runBlocking: ${Thread.currentThread().name}")
}