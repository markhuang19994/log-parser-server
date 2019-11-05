(function () {
    let isCtrlHold = false;
    let isShiftHold = false;

    $(document).keyup(function (e) {
        if (e.which === 17) //17 is the code of Ctrl button
            isCtrlHold = false;
        if (e.which === 16) //16 is the code of Shift button
            isShiftHold = false;
    });
    $(document).keydown(function (e) {
        if (e.which === 17)
            isCtrlHold = true;
        if (e.which === 16)
            isShiftHold = true;

        ShortcutManager(e);
    });

    const keyMap = {
        ctrl: {},
        ctrlShift: {}
    };

    window.setControlKeyFunction = function (key, callBack) {
        keyMap.ctrl[key.toUpperCase().codePointAt(0)] = callBack;
    };

    window.setControlShiftKeyFunction = function (key, callBack) {
        keyMap.ctrlShift[key.toUpperCase().codePointAt(0)] = callBack;
    };

    function ShortcutManager(e) {
        const keyCode = e.which;
        if (isCtrlHold && isShiftHold) {
            if (keyMap.ctrlShift[keyCode]) {
                e.preventDefault();
                keyMap.ctrlShift[keyCode]();
            }
            return;
        }
        if (isCtrlHold) {
            if (keyMap.ctrl[keyCode]) {
                e.preventDefault();
                keyMap.ctrl[keyCode]();
            }
        }
    }
})();
