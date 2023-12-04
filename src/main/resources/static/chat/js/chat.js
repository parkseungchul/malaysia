document.getElementById("send-btn").addEventListener("click", sendMessage);

document.getElementById("chat-input").addEventListener("keypress", function(event) {
    if (event.key === "Enter") {
        sendMessage();
    }
});

document.getElementById("reset-btn").addEventListener("click", function() {

    fetch('/chat/ajax', {
        method: 'GET'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not OK');
            }
            // 서버로부터의 응답이 성공적인 경우의 처리를 추가할 수 있음
            var chatBox = document.getElementById("chat-box");
            chatBox.innerHTML = '';

            console.log("Reset request was successful");
        })
        .catch(error => {
            console.error('Fetch error:', error);
        });
});


function sendMessage() {
    var inputElement = document.getElementById("chat-input");
    var sendButton = document.getElementById("send-btn");
    var loader = document.querySelector(".loader");
    var message = inputElement.value.trim();

    if (!message) {
        return;
    }

    sendButton.classList.add("loading");
    sendButton.disabled = true; // 버튼 비활성화

    addMessage(message, "user-message");
    inputElement.value = "";

    // 로딩 인디케이터 표시 및 버튼 비활성화
    loader.style.display = "inline-block";
    sendButton.disabled = true;

    // 서버에 메시지 전송 (/sample/ajax 엔드포인트 사용)
    fetch('/chat/ajax', {
        method: 'POST',
        headers: {
            'Content-Type': 'text/plain',
        },
        body: message
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Server response was not OK');
            }
            return response.json();
        })
        .then(data => {
            addMessage(data.data, "server-message");
        })
        .catch(error => {
            console.error('Error:', error);
            addMessage(error.data, "server-message");
        })
        .finally(() => {
            // 로딩 인디케이터 숨기기 및 버튼 활성화
            sendButton.classList.remove("loading");
            sendButton.disabled = false; // 버튼 활성화
        });
}

function addMessage(text, className) {
    var chatBox = document.getElementById("chat-box");
    var newMessage = document.createElement("div");
    newMessage.innerHTML = text; // innerHTML을 사용하여 HTML 태그 처리
    //newMessage.textContent = text;  //text만 가져옴
    newMessage.classList.add(className);
    chatBox.appendChild(newMessage);

    // 채팅창을 최신 메시지 위치로 자동 스크롤
    chatBox.scrollTop = chatBox.scrollHeight;
}

// Ajax를 사용하여 contentList 데이터를 서버에서 가져오는 함수
function getContentList() {
    return fetch('/chat/ajax/list') // 서버의 엔드포인트에 맞게 변경
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not OK');
            }
            return response.json();
        })
        .then(data => {
            return data; // 서버에서 반환한 데이터 그대로 반환
        })
        .catch(error => {
            console.error('Fetch error:', error);
            return []; // 오류 발생 시 빈 배열 반환 또는 처리 방식을 선택하세요.
        });
}

// 페이지 로드 시 contentList 가져오고 처리하기
document.addEventListener('DOMContentLoaded', function () {
    getContentList()
        .then(contentList => {
            // contentList가 null이거나 undefined인 경우 빈 배열로 처리
            contentList = contentList || [];

            // contentList를 사용하여 JavaScript로 데이터 채우기
            var chatBox = document.getElementById("chat-box");
            contentList.forEach(function (message) {
                var messageDiv = document.createElement("div");
                console.log(message.role + " "+ message.content);
                messageDiv.textContent = message.content;
                messageDiv.className = message.role === "user" ? "user-message" : "server-message";
                chatBox.appendChild(messageDiv);
            });

            // 스크롤을 항상 최하단으로 이동
            chatBox.scrollTop = chatBox.scrollHeight;
        });
});

// JavaScript 코드
function createSnowflake() {
    const snowflake = document.createElement("div");
    snowflake.className = "snowflake";
    snowflake.style.left = `${Math.random() * 100}vw`;
    document.getElementById("snowfall").appendChild(snowflake);

    snowflake.addEventListener("animationiteration", () => {
        snowflake.style.left = `${Math.random() * 100}vw`;
        snowflake.style.animationDuration = `${Math.random() * 5 + 2}s`;
    });

    setTimeout(() => {
        snowflake.remove();
    }, 8000);
}

setInterval(createSnowflake, 500); // 눈송이 생성 간격 조절
