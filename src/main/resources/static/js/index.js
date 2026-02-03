document.addEventListener("DOMContentLoaded", () => {
    let emailForm  = document.getElementById("email-input");
    let emailValid = document.getElementById("email-valid");
    trackBtn = document.getElementById("track-btn");

    trackBtn.disabled = true;
    const emailReg = /^[a-zA-Z0-9]{2,}(?:\.[a-zA-Z0-9]+)*@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    emailForm.addEventListener("input", () => {
        const emailInput = emailForm.value.trim();
        const emailTest = emailReg.test(emailInput);
        if(!emailTest) {
            emailValid.style.display = "block";
            emailValid.innerText = "email 형식이 올바르지 않습니다.";
        } else {
            emailValid.style.display = "none";
            trackBtn.disabled = false;
    }
    });

    document.getElementById("track-btn").addEventListener("click", async () => {
        await result();
    });
});

async function result(){
    url = '/api/search/tracker'
    const hblNo = document.getElementById("hblNo-input").value;
    const email = document.getElementById("email-input").value;
    const blYear = document.getElementById("blYear").value;

    if(!hblNo || !email){
      alert("운송장번호와 이메일을 입력해주세요");
      return;
    }

    const requestData = {
        hblNo: hblNo,
        email: email,
        blYear: parseInt(blYear)
    }
    try{
        document.getElementById('loading-result').style.display = 'block';
        document.getElementById('result-section').style.display = 'none';

        const res = await fetch(url, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        })
        if(!res.ok){
            const msg = await res.text();
            throw new Error(msg || "서버 통신 오류");
        }
        const data = await res.json();
        const tbody = document.getElementById("track-result-body");
        tbody.innerHTML = "";
        let rows="";
        if(data.trackHistory && data.trackHistory.length > 0){
            data.trackHistory.forEach(history => {
            rows += `
                <tr>
                    <td>${history.processingTime}</td>
                    <td>${history.status}</td>
                </tr>
                `;
            });
            tbody.innerHTML = rows;
        } else {
            tbody.innerHTML = `
                <tr><td colspan="2">조회된 이력이 없습니다.</td></tr>
            `;
        }
        document.getElementById('loading-result').style.display = 'none';
        document.getElementById('result-section').style.display = 'block';
    } catch (error){
        console.error("Error:", error);
        document.getElementById('loading-result').style.display = 'none';
    }
}
