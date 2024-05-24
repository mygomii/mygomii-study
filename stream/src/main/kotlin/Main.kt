import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
//    startColdStream()
    startHotStream()
}

fun startColdStream() = runBlocking {
    // 콜드 스트림 생성
    val coldFlow: Flow<String> = flow {
        println("Flow is started")
        emit("Hello")
        emit("World")
    }

    // 구독자가 없으므로 스트림은 실행되지 않음
    println("Flow is created but not collected yet")

    // 스트림을 구독
    val job1 = launch {
        coldFlow.collect { value ->
            println("Subscriber 1 received: $value")
        }
    }

    // 두 번째 구독자 (3초 후 구독 시작)
    val job2 = launch {
        println("start job2")
        delay(3000)
        coldFlow.collect { value ->
            println("Subscriber 2 received: $value")
        }
    }

    // 모든 작업이 완료될 때까지 대기
    joinAll(job1, job2)
}


fun startHotStream() = runBlocking {
    val sharedFlow = MutableSharedFlow<String>()

    // 데이터 방출 코루틴
    val emitterJob = launch {
        repeat(10) {
            delay(1000) // 1초마다 데이터 방출
            sharedFlow.emit("Tick: $it")
            println("Emitting: Tick: $it")
        }
    }

    // 첫 번째 구독자
    val subscriberJob1 = launch {
        sharedFlow.collect { value ->
            println("Subscriber 1 received: $value")
        }
    }

    // 두 번째 구독자 (5초 후 구독 시작)
    val subscriberJob2 = launch {
        delay(5000)
        sharedFlow.collect { value ->
            println("Subscriber 2 received: $value")
        }
    }

    // 모든 작업이 완료될 때까지 대기
    joinAll(emitterJob, subscriberJob1, subscriberJob2)
}