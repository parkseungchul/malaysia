const questions = [
    { id: 1, question: "수고해주셔서 감사합니다.", answer: "Thanks for taking the trouble." },
    { id: 2, question: "시간내주셔서 감사합니다.", answer: "Thanks for taking the time." },
    // ... 여기에 더 많은 질문 추가 ...
    { id: 3, question: "그것에 너무 실망하지마.", answer: "Don't let it get you down." }
];

let currentQuestionIndex = 0;
let timer;
const totalDuration = 5;

function showQuestion() {
    const questionElement = document.querySelector('.question');
    const answerElement = document.querySelector('.answer');
    const progressBar = document.querySelector('.progress-bar');

    // 프로그래스바 초기화 및 transition 설정
    progressBar.style.transition = 'none';
    progressBar.style.width = '0%';

    setTimeout(() => {
        progressBar.style.transition = ''; // transition을 다시 활성화
    }, 0);

    answerElement.style.display = 'none';
    questionElement.textContent = questions[currentQuestionIndex].question;

    startTimer();
}

function startTimer() {
    disableAnswerButtons();
    let timeLeft = totalDuration;
    timer = setInterval(() => {
        timeLeft--;
        updateProgressBar(timeLeft);

        if (timeLeft <= 0) {
            clearInterval(timer);
            // 타이머가 종료되고 일정 시간 후에 정답을 표시
            setTimeout(showAnswer, 1000); // 1000ms 지연
            setTimeout(enableAnswerButtons, 1000); // 1000ms 지연

        }
    }, 1000);
}

function showAnswer() {
    const answerElement = document.querySelector('.answer');
    answerElement.textContent = '' + questions[currentQuestionIndex].answer;
    answerElement.style.display = 'block';
}

function updateProgressBar(timeLeft) {
    const progressBar = document.querySelector('.progress-bar');
    let width = ((totalDuration - timeLeft) / totalDuration) * 100;
    progressBar.style.width = `${width}%`;
}

function nextQuestion() {
    clearInterval(timer);
    currentQuestionIndex++;
    if (currentQuestionIndex < questions.length) {
        showQuestion();
    } else {
        showResults();
    }
}

function disableAnswerButtons() {
    document.querySelectorAll('.btn-answer').forEach(button => {
        button.disabled = true;
    });
}

function enableAnswerButtons() {
    document.querySelectorAll('.btn-answer').forEach(button => {
        button.disabled = false;
    });
}


let userResponses = [];

function handleUserResponse(isCorrect) {
    userResponses.push({
        questionId: questions[currentQuestionIndex].id,
        isCorrect: isCorrect
    });
    nextQuestion();
}

function showResults() {
    const correctCount = userResponses.filter(response => response.isCorrect).length;
    const incorrectCount = userResponses.length - correctCount;

    // 결과 텍스트 업데이트
    document.getElementById('know-count').textContent = `I know: ${correctCount}`;
    document.getElementById('unknow-count').textContent = `I don't know: ${incorrectCount}`;

    // 퀴즈 컨테이너 숨기기
    document.getElementById('quiz-container').style.display = 'none';

    // 결과 컨테이너 표시
    document.getElementById('result-container').style.display = 'block';

    // 이벤트 핸들러 설정
    document.getElementById('review-button').addEventListener('click', function() {
        // 결과창 숨기기
        document.getElementById('result-container').style.display = 'none';

        // 퀴즈 컨테이너 표시
        document.getElementById('quiz-container').style.display = 'block';

        // 퀴즈 재시작
        currentQuestionIndex = 0;
        userResponses = []; // 사용자 응답 초기화
        showQuestion();
    });

    // 'Next' 버튼 이벤트 핸들러 설정
    document.getElementById('next-button').addEventListener('click', function() {
        // 다음 퀴즈 또는 학습 단계로 이동
        alert('Loading next quiz or learning stage...');
    });
}

document.addEventListener('DOMContentLoaded', (event) => {
    showQuestion();
});

