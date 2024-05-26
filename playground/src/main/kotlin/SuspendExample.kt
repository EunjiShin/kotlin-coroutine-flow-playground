import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// suspension point를 만드는 delay는 코루틴 또는 suspend fun 내에서만 호출할 수 있다.
suspend fun doThree() {
    println("launch1 : ${Thread.currentThread().name}")
    delay(1000L)
    println("3!")
}

suspend fun doTwo() {
    println("runBlocking : ${Thread.currentThread().name}")
    delay(500L)
    println("2!")
}

// delay가 없어서 suspend modifier를 굳이 붙일 필요가 없음
fun doOne() {
    println("launch1 : ${Thread.currentThread().name}")
    println("1!")
}

// runBlocking<Int> 등을 이용해 코루틴 반환타입을 지정할 수 있다.
fun main() = runBlocking<Unit> {
    launch {
        // launch 블럭 내에서 this는 코루틴.
        // launch는 코루틴 내에서만 유효하다 -> runBlocking 블럭 밖에서 선언되었다면 동작하지 않음
        doThree()
    }
    launch {
        doOne()
    }
    doTwo()
    3
}