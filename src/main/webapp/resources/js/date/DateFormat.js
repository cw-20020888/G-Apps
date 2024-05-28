/**
 * 날짜를 정해진 형태로 출력한다.
 */
function DateFormat() {
}
/**
 * 날짜를 정해진 형태로 변환한다.
 * 
 * @param date
 *            변환하려는 날짜
 * @param pattern
 *            변환하려는 형태
 * @retrn 변환된 값
 */
DateFormat.format = function(date, pattern) {
	if (!date) {
		return '';
	}
	if (!pattern) {
		return date.getTime();
	}
	pattern = DateFormat.formatLocale(date, pattern);
	pattern = pattern.replace(/yyyy/g, date.getFullYear());
	pattern = pattern.replace(/YY/g, (date.getFullYear()) ? String(date.getFullYear()).substring(2) : date
			.getFullYear());
	pattern = pattern.replace(/MM/g, (date.getMonth() + 1 < 10 ? '0' : '') + (date.getMonth() + 1));
	pattern = pattern.replace(/dd/g, (date.getDate() < 10 ? '0' : '') + date.getDate());
	pattern = pattern.replace(/EEEE/g, DateFormat.LONG_WEEKDAYS[date.getDay()]);
	pattern = pattern.replace(/EEE/g, DateFormat.MEDIUM_WEEKDAYS[date.getDay()]);
	pattern = pattern.replace(/EE/g, DateFormat.SHORT_WEEKDAYS[date.getDay()]);
	pattern = pattern.replace(/HH/g, (date.getHours() < 10 ? '0' : '') + date.getHours());
	pattern = pattern.replace(/mm/g, (date.getMinutes() < 10 ? '0' : '') + date.getMinutes());
	pattern = pattern.replace(/ss/g, (date.getSeconds() < 10 ? '0' : '') + date.getSeconds());
	pattern = pattern.replace(/Q/g, (parseInt(date.getMonth() / 3) + 1) + '/4 분기');
	return pattern;
}
DateFormat.formatLocale = function(date, pattern) {
	pattern = pattern.replace(/YYYY/g, date.getFullYear() + '년');
	pattern = pattern.replace(/MMMM/g, (date.getMonth() + 1) + '월');
	pattern = pattern.replace(/DDDD/g, date.getDate() + '일');
	return pattern;
}
DateFormat.LONG = 'yyyy/MM/dd HH:mm:ss';
DateFormat.SHORT_WEEKDAYS = ['일', '월', '화', '수', '목', '금', '토'];
DateFormat.MEDIUM_WEEKDAYS = ['일', '월', '화', '수', '목', '금', '토'];
DateFormat.LONG_WEEKDAYS = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'];
DateFormat.MODALTITLE = ' 달력  ';
DateFormat.ENG_SHORT_WEEKDAYS = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
DateFormat.ENG_MEDIUM_WEEKDAYS = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
