$(function () {
    const modal = document.getElementById("myModal");
    const closeSpan = modal.querySelector('.close');
    const modelP = document.getElementById('modal-content__p-1');
    const oriStyle = JSON.parse(JSON.stringify(modelP.style));
    let callBack;

    window.showPopup = (text, cb) => {
        callBack = null;
        modelP.innerText = text;
        modelP.style = JSON.parse(JSON.stringify(oriStyle));
        if (typeof cb === 'function') {
            callBack = cb
        }
    };

    window.showPopupHtml = (html, style, cb) => {
        if (typeof html === 'object') {
            modelP.innerHTML = '';
            modelP.appendChild(html);
        } else {
            modelP.innerHTML = html;
        }
        Object.assign(modelP.style, style);
        modal.style.display = "block";
        if (typeof cb === 'function') {
            callBack = cb
        }
    };

    window.closePopup = () => {
        callBack = null;
        modelP.style = JSON.parse(JSON.stringify(oriStyle));
        $(closeSpan).trigger('click');
    };

    closeSpan.addEventListener('click', onClose);

    $(document).on('click', 'body', e => {
        if (e.target === modal) {
            onClose()
        }
    });

    function onClose() {
        modal.style.display = "none";
        if (callBack && typeof callBack === 'function') {
            callBack();
            callBack = null;
        }
    }
});
