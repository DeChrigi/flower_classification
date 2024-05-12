function checkFiles(files) {
    console.log(files);

    if (files.length != 1) {
        alert("Bitte genau eine Datei hochladen.")
        return;
    }

    const fileSize = files[0].size / 1024 / 1024; // in MiB
    if (fileSize > 10) {
        alert("Datei zu gross (max. 10Mb)");
        return;
    }

    answerPart.style.visibility = "visible";
    const file = files[0];

    // Preview
    if (file) {
        preview.src = URL.createObjectURL(files[0])
    }

    // Upload
    const formData = new FormData();
    for (const name in files) {
        formData.append("image", files[name]);
    }

    fetch('/analyze', {
        method: 'POST',
        headers: {
        },
        body: formData
    }).then(
        response => {
            console.log(response)
            response.json().then(function (data) {
                const container = document.getElementById('answer');
                container.innerHTML = ""; // Löscht den vorherigen Inhalt
                data.forEach(item => {
                    const entry = document.createElement('div');
                    entry.className = 'result-entry';
            
                    const label = document.createElement('p');
                    label.textContent = `${item.label}, Wahrscheinlichkeit: ${item.probability}`;
            
                    const image = document.createElement('img');
                    image.src = item.imagePath;
                    image.alt = `Bild von ${item.imagePath}`;
                    image.width = 200; // Breite anpassen
                    image.height = 200; // Höhe anpassen
            
                    entry.appendChild(label);
                    entry.appendChild(image);
                    container.appendChild(entry);
                });
            });

        }
    ).then(
        success => console.log(success)
    ).catch(
        error => console.log(error)
    );

}