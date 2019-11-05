String.prototype.hashCode = function () {
    let hash = 0, i, chr;
    if (this.length === 0) return hash;
    for (i = 0; i < this.length; i++) {
        chr = this.charCodeAt(i);
        hash = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
    }
    return hash;
};

function escapeRegex(str) {
    const specialChars = ['[', '\\', '.', '|', '*', '+', '$', '^', '&', '(', ')', '{', ' }'];
    return Array.from(str)
        .map(s => {
            if (specialChars.includes(s)) {
                return '\\' + s;
            }
            return s;
        }).join('');
}
