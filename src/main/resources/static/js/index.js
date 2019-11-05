$(function () {
    function fillLogContainer(logAttrList) {
        const logContainer = document.getElementsByClassName('log-container')[0];
        logContainer.innerHTML = '';
        logAttrList.forEach(attr => {
            const logBlock = document.createElement('div');
            logBlock.classList.add('log-block');
            for (let key of Object.keys(attr)) {
                const span = document.createElement('span');
                span.classList.add(key);
                span.classList.add('log-attr');
                if (key === 'time') {
                    span.innerText = attr[key];
                } else if (key === 'content') {
                    const idx = attr[key].indexOf('\n');
                    if (idx !== -1) {
                        const firstLine = attr[key].substr(0, idx);
                        const otherLine = attr[key].substr(idx + 1);
                        span.innerText = firstLine;
                        span.appendChild(document.createElement('br'));
                        const pre = document.createElement('pre');
                        pre.innerText = otherLine;
                        span.appendChild(pre);
                    } else {
                        span.innerText = attr[key];
                    }
                } else {
                    span.innerText = `[${attr[key]}]`;
                }
                logBlock.appendChild(span);
            }
            logContainer.appendChild(logBlock);
        })
    }

    function getCurrentLogDetails(callback) {
        $.ajax({
            url: '/get/current-log-details',
            method: 'POST',
            success: d => callback(d),
            error: e => console.error(e)
        });
    }

    class MainConfig {
        constructor() {
            $(document).on('click', 'button[name="main-config-btn"]', e => {
                closePopup();
                startLoading('Loading...');
                const val = document.querySelector('select[name="main-config"]').value;
                $.ajax({
                    url: '/set/main_args',
                    method: 'POST',
                    data: {data: val},
                    success: d => {
                        console.log(d);
                        getCurrentLogDetails(d => fillLogContainer(d.map(x => x['attributeMap'])));
                    },
                    error: e => console.error(e),
                }).done(() => endLoading());
            });
        }

        init() {
            $.ajax({
                url: '/get/main_args',
                success: d => {
                    const div = document.createElement('div');
                    const sel = document.createElement('select');
                    const keys = Object.keys(d);
                    for (let i = 0; i < keys.length; i++) {
                        const opt = new Option();
                        opt.value = d[keys[i]];
                        opt.innerText = keys[i];
                        sel.appendChild(opt);
                    }
                    sel.name = 'main-config';
                    div.appendChild(sel);
                    div.appendChild($('<button name="main-config-btn">submit</button>')[0]);
                    Object.assign(div.style, {
                        display: 'flex',
                        justifyContent: 'space-evenly',
                        marginTop: '4px',
                    });
                    showPopupHtml(div);
                },
                error: e => {
                    console.error(e);
                }
            });
        }
    }

    class ActionExecutor {
        constructor() {
            setControlShiftKeyFunction('A', () => {
                const ipt = '<input name="action"/>';
                showPopupHtml('<span>Action: </span>' + ipt);
            });

            $(document).on('change', 'input[name="action"]', e => {
                const val = e.currentTarget.value;
                const actionIdx = val.indexOf(':');
                if (actionIdx === -1) {
                    showPopup('Action 格式不正確 => Action: Params');
                    return false;
                }

                const action = val.substr(0, actionIdx).trim().toLocaleLowerCase();
                const params = val.substr(actionIdx + 1);

                if (['fmethod', 'cmethod', 'life', 'format'].includes(action)) {
                    closePopup();
                    this.execAction(action, params);
                } else {
                    showPopup('找不到Action: ' + action);
                }
            });
        }

        execAction(action, params) {
            $.ajax({
                url: '/exec/instruct/' + action,
                method: 'POST',
                data: {data: params},
                success: d => {
                    console.log(d);
                    getCurrentLogDetails(d => fillLogContainer(d.map(x => x['attributeMap'])));
                },
                error: e => console.error(e)
            });
        }
    }

    class Heilight {
        heightLightStrMap = {};

        constructor() {
            setControlKeyFunction('H', () => {
                const heightLightStr = window.getSelection().toString();
                const hash = heightLightStr.hashCode();
                heightLightStrMap[hash] = heightLightStr;

                const divs = new Array(5).fill('').map((x, i) => {
                    return `<div class="log demo bg-color-${i + 1}" data-hash='${hash}' data-color='${i + 1}'></div>`;
                }).join('\n');
                showPopupHtml(divs, {
                    display: 'flex',
                    justifyContent: 'center'
                });
            });

            $(document).on('click', 'div.log.demo', e => {
                const hash = e.currentTarget.dataset['hash'];
                const bgColor = e.currentTarget.dataset['color'] || 1;
                if (!hash) return false;
                this.heightLightWord(heightLightStrMap[hash], bgColor);
                delete this.heightLightStrMap[hash];
                closePopup();
            });
        }

        heightLightWord(word, colorIndex) {
            $(".log-container").markRegExp(new RegExp(escapeRegex(word), 'g'), {
                "separateWordSearch": true, "diacritics": true, "debug": false,
                "className": `log bg-color-${colorIndex}`
            });
        }
    }

    (function main() {
        const mainConfig = new MainConfig();
        mainConfig.init();
        setControlShiftKeyFunction('R', () => mainConfig.init());

        new ActionExecutor();
        new Heilight();
    })();

});
