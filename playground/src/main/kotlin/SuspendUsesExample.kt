import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

// kotlin의 suspend == 다른 언어의 async
// 코틀린은 suspend 함수를 호출하기 위해 어떤 키워드도 필요하지 않다.

suspend fun getRandom1(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

suspend fun getRandom2(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

suspend fun getRandom3(): Int {
    try {
        delay(1000L)
        return Random.nextInt(0, 500)
    } finally {
        println("getRandom1 is cancelled.")
    }
}

suspend fun getRandom4(): Int {
    delay(500L)
    throw IllegalStateException()
}

suspend fun doSomething() = coroutineScope {
    val value1 = async { getRandom3() }
    val value2 = async { getRandom4() }
    try {
        println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
    } finally {
        println("doSomething is cancelled.")
    }
}

fun main() = runBlocking {
    // async를 이용해 동시에 다른 블록을 수행할 수 있다.
    // await 키워드를 만나면 async 블록의 수행이 종료되었는지 확인하고, 끝나지 않았다면 suspend 되었다가 이후 깨어나 반환값을 받는다.
    // async는 결과를 받아야 할 때, launch는 결과를 받지 않아도 될 때 사용한다.
    val elapsedTime = measureTimeMillis {
        val value1 = async { getRandom1() }
        val value2 = async { getRandom2() }
        println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
    }
    println(elapsedTime)

    // async(start = CoroutineStart.LAZY)를 이용하면 우리가 원하는 순간에 start를 이용해 코드 블록을 수행하게 만들 수 있다.
    val elapsedTime2 = measureTimeMillis {
        val value1 = async(start = CoroutineStart.LAZY) { getRandom1() }
        val value2 = async(start = CoroutineStart.LAZY) { getRandom2() }

        value1.start()
        value2.start()

        println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}")
    }
    println(elapsedTime2)

    // 코드 블럭 실행 중 예외가 발생하면, 위쪽의 코루틴 스코프와 아래쪽의 코루틴 스코프가 모두 취소된다.
    // 이 경우, getRandom4에서 오류가 발생해 getRandom3과 doSomething이 취소되었다.
    try {
        doSomething()
    } catch (e: IllegalStateException) {
        println("doSomething failed: $e")
    }
}