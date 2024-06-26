import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        println(coroutineContext) // 앞으로 코루틴이 실행될 방식을 담고 있다.
        launch {
            // runBlocking 블럭 안에 launch 블럭이 있다.
            // 따라서 launch는 Queue에 작업을 담아두고, runBlocking의 코드들이 메인 스레드를 다 사용할 때까지 기다린다.
            println("launch: ${Thread.currentThread().name}")
            println("World!")
        }
        println("runBlocking: ${Thread.currentThread().name}")
        delay(500L)    // suspension point. 해당 스레드를 해제하고 잠시 쉰다 -> 다른 스레드에 순서를 양보할 수 있다. 이 예제는 하나의 스레드에서 두 코루틴이 존재하니 블럭간 양보가 발생한 사례.
        Thread.sleep(500) // 다른 코루틴에게 기회를 주는게 아니라, 내 루틴에서 500ms를 쉬어간다.
        // delay는 현재 스레드를 다른 코드에게 양보하고, sleep은 양보를 하지 않는다는 차이!
        println("Hello")
    }
    // 상위 코루틴은 하위 코루틴을 끝까지 책임진다.
    // 이 경우, runBlocking은 하위 코루틴인 두 개의 launch가 끝날 때까지 종료되지 않는다.
    // 같은 맥락으로, 부모 코루틴이 cancel 되면 자식 코루틴도 cancel 된다.
    print("4")
}