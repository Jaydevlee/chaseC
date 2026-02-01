document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("track-btn").addEventListener("click", async () => {
        await result();
    });
});

async function result(){
    url = '/api/search/create_tracker'
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
        const dateStr = data.updatedAt ? new Date(data.updatedAt).toLocaleString() : "방금 전";
        const tbody = document.getElementById("track-result-body");
        tbody.innerHTML = "";
        tbody.innerHTML = `
                    <tr>
                        <td>${dateStr}</td>
                        <td>${data.status}</td>
                    </tr>
                    `;
        document.getElementById('loading-result').style.display = 'none';
        document.getElementById('result-section').style.display = 'block';
    } catch (error){
        console.error("Error:", error);
        document.getElementById('loading-result').style.display = 'none';
    }
}
