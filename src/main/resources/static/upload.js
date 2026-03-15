async function uploadFile(){

    const file = document.getElementById("fileInput").files[0];
    const limit = document.getElementById("limit").value;
    const expiry = document.getElementById("expiry").value;

    if(!file){
        alert("Please select a file");
        return;
    }

    document.getElementById("status").innerText = "Uploading file...";

    const formData = new FormData();

    formData.append("file", file);
    formData.append("limit", limit);
    formData.append("expiry", expiry);

    const response = await fetch("/upload", {
        method: "POST",
        body: formData
    });

    const text = await response.text();

    document.getElementById("status").innerText = "Upload successful";

    const link = text.split("Download link: ")[1];

    document.getElementById("downloadLink").value = link;

    document.getElementById("linkBox").classList.remove("hidden");
}

function copyLink(){

    const input = document.getElementById("downloadLink");

    input.select();
    document.execCommand("copy");

    const msg = document.getElementById("copyMessage");

    msg.innerText = "Link copied successfully!";

    // message disappears after 2 seconds
    setTimeout(() => {
        msg.innerText = "";
    }, 2000);
}