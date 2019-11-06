$(function () {

    class LogContainer {
        start = 0;
        end = 0;
        limit = 0;
        logContainer;

        constructor(logContainer) {
            this.logContainer = logContainer;
        }

        fillLogContainer(logDetails) {
            const logAttrList = logDetails.map(x => x['attributeMap']);
            const logContainer = this.logContainer;
            logContainer.innerHTML = '';
            logAttrList.forEach(attr => {
                const logBlock = document.createElement('div');
                logBlock.classList.add('log-block');
                for (let key of Object.keys(attr)) {
                    const pre = document.createElement('pre');
                    pre.classList.add(key);
                    pre.classList.add('log-attr');
                    pre.classList.add('inline');
                    const attrVal = attr[key];
                    if (key === 'time') {
                        pre.innerText = attrVal;
                    } else if (key === 'content') {
                        const idx = attrVal.indexOf('\n');
                        if (idx !== -1) {
                            const firstLine = attrVal.substr(0, idx);
                            const otherLine = attrVal.substr(idx + 1);
                            pre.innerText = firstLine;
                            pre.appendChild(document.createElement('br'));
                            const contentPre = document.createElement('pre');
                            contentPre.innerText = otherLine;
                            pre.appendChild(contentPre);
                        } else {
                            pre.innerText = attrVal;
                        }
                    } else {
                        pre.innerText = `[${attrVal}]`;
                    }
                    logBlock.appendChild(pre);
                }
                logContainer.appendChild(logBlock);
            })
        }

        getCurrentLogDetails() {
            return new Promise((res, rej) => {
                $.ajax({
                    url: '/get/current-log-details',
                    method: 'POST',
                    success: d => res(d),
                    error: e => rej(e)
                });
            })
        }
    }

    class MainConfig {
        logContainer;
        constructor(logContainer) {
            this.logContainer = logContainer;
            $(document).on('click', 'button[name="main-config-btn"]', () => {
                closePopup();
                startLoading('Loading...');
                const val = document.querySelector('select[name="main-config"]').value;
                $.ajax({
                    url: '/set/main_args',
                    method: 'POST',
                    data: {data: val},
                    success: async d => {
                        console.log(d);
                        const res = await this.logContainer.getCurrentLogDetails();
                        this.logContainer.fillLogContainer(res['logDetails']);
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
        logContainer;
        constructor(logContainer) {
            this.logContainer = logContainer;
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

                if (['fmethod', 'cmethod', 'gmethod', 'life', 'format'].includes(action)) {
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
                success: async d => {
                    console.log(d);
                    const res = await this.logContainer.getCurrentLogDetails();
                    this.logContainer.fillLogContainer(res['logDetails']);
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
                this.heightLightStrMap[hash] = heightLightStr;

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
                this.heightLightWord(this.heightLightStrMap[hash], bgColor);
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
        const logContainer = new LogContainer(document.getElementsByClassName('log-container')[0]);
        const mainConfig = new MainConfig(logContainer);
        mainConfig.init();
        setControlShiftKeyFunction('R', () => mainConfig.init());

        new ActionExecutor(logContainer);
        new Heilight();

    })();

});
