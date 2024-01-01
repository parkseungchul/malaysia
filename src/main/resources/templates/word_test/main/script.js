const questions = [
    { id: 1, question: "Question 1", answer: "Answer 1" },
    // ... 나머지 질문과 답변 추가 ...
    { id: 10, question: "Question 10", answer: "Answer 10" }
];

document.addEventListener('DOMContentLoaded', function() {
    const questionsContainer = document.getElementById('questions-container');

    questions.forEach((item, index) => {
        const questionDiv = document.createElement('div');
        questionDiv.classList.add('card');

        const questionHeader = document.createElement('div');
        questionHeader.classList.add('card-header');
        questionHeader.setAttribute('id', `heading${index}`);
        questionHeader.innerHTML = `<button class="btn btn-link" type="button" data-toggle="collapse" data-target="#collapse${index}" aria-expanded="false" aria-controls="collapse${index}">${item.question}</button>`;
        questionDiv.appendChild(questionHeader);

        const collapseDiv = document.createElement('div');
        collapseDiv.id = `collapse${index}`;
        collapseDiv.classList.add('collapse');
        collapseDiv.setAttribute('aria-labelledby', `heading${index}`);
        collapseDiv.setAttribute('data-parent', '#questions-container'); // 모든 collapseDiv에 동일한 data-parent 설정
        const cardBody = document.createElement('div');
        cardBody.classList.add('card-body');
        cardBody.textContent = item.answer;
        collapseDiv.appendChild(cardBody);

        questionDiv.appendChild(collapseDiv);

        questionsContainer.appendChild(questionDiv);
    });
});





