document.addEventListener("DOMContentLoaded", () => {
    url = '/api/search/create_tracker'
    const hblNo = document.getElementById("hblNo-input").value;
    const email = document.getElementById("email-input").value;
    const blYear = document.getElementById("blYear").value;

    document.getElementById("track-btn").addEventListener("click", async () => {
        await result();
    };
})
    async function result(){
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
                header: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            })
            if(!res.ok){
                const msg = await res.text();
                alert("Error: " + msg);
            }
            const data = await res.json();
            const dateStr = data.updatedAt ? new Date(data.updatedAt).toLocaleString() : "방금 전";
            const tbody = document.getElementById("track-result-body");
            tbody.innerHTML = "";
            tbody.innerHTML = `
                        <tr>
                            <td>${data.dateStr}</td>
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
