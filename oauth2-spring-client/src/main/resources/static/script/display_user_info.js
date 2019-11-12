function createRow(headerData, data) {
    var trElem = document.createElement('tr');
    trElem.classList.add('row', 'data');

    trElem.appendChild(createCell(headerData, 'th'));
    trElem.appendChild(createCell(data, 'td'));

    return trElem;
}

function createCell(innerHtml, cellType) {
    var tdElem = document.createElement(cellType);
    // tdElem.classList.add('cell', clazz);
    tdElem.innerHTML = innerHtml;
    return tdElem;
}

function renderUserInfo(userInfo) {
    var existingTableElem = document.getElementById('userInfoTable');
    var newTableElem = document.createElement('table');
    newTableElem.id = 'userInfoTable';
    newTableElem.classList.add('userInfoTable');

    newTableElem.appendChild(createRow('Username', userInfo.username));
    newTableElem.appendChild(createRow('Authorities', userInfo.authorities));
    newTableElem.appendChild(createRow('Client Id', userInfo.clientId));
    newTableElem.appendChild(createRow('Scope', userInfo.scope));

    if (existingTableElem) {
        existingTableElem.parentNode.removeChild(existingTableElem);
    }
    document.body.appendChild(newTableElem);
}

function retrieveAndRender() {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/user');
    xhr.onload = function () {
        if (xhr.status === 200) {
            try {
                var tokenInfoListe = JSON.parse(xhr.response);
                renderUserInfo(tokenInfoListe);
            } catch (e) {
                console.log("Feil ved parsing/rendering av resultat fra tjenestekall");
            }
        } else {
            console.log("Forventet HTTP 200, fikk " + xhr.status);
        }
    };
    xhr.onerror = function () {
        console.log("Ukjent feil ved kall til tjeneste");
    };
    xhr.send();

    var xhrData = new XMLHttpRequest();
    xhrData.open('GET', '/data');
    xhrData.onload = function () {
        if (xhrData.status === 200) {
            try {
                document.getElementById('userInfoTable').appendChild(createRow('Backend data', xhrData.response))
            } catch (e) {
                console.log("Feil ved parsing/rendering av resultat fra tjenestekall");
            }
        } else {
            console.log("Forventet HTTP 200, fikk " + xhrData.status);
        }
    };
    xhrData.onerror = function () {
        console.log("Ukjent feil ved kall til tjeneste");
    };
    xhrData.send();
}

retrieveAndRender(); // Hent initielle data
