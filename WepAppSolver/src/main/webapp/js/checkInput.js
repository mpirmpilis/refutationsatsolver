// checks input before sending to java code to be solved
function checkInputForm() {
	let s = document.getElementById("txt1").value.replace(/\s+/g,"");  // delete all whitespaces
	
	if (s === "" || s === null) {
		alert("Empty input. Please type a problem in correct form to solve");
		return false;
	} else if (s.charAt(0) != '{' && s.charAt(0) != '/') {
		alert("Missing left bracket { or slash /.");
		return false;
	} else if (s.charAt(0) === '{' && s.indexOf('}') === -1) {
		alert("Missing right bracket } bracket.");
		return false;
	} else if (s.charAt(0) === '/' && (s.indexOf('{') > -1 || s.indexOf('}') > -1)) {
		alert("Error : Cannot insert brackets when input starts with slash /");
		return false;
	} else {
		var i, openPar = 0;	
		
		for (i = 0; i < s.length; i++) {
			if (s.charAt(i) == '(')
				openPar++;
			else if (s.charAt(i) == ')')
				openPar--;
		}
		if (openPar != 0) {
			alert("Parenthesis pairs not matching.");
			return false;
		} else {
			var validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz ,/<-&|~(){}>";
			var rightBracketCounter = 0;
			
			for (i = 1; i < s.length; i++) {
				if (s.charAt(i) === ',' && s.charAt(i+1) === ',') {
					alert("Error : Cannot have two or more consecutive commas ','");
		     		return false;
				} else if (s.charAt(i) === '-' && s.charAt(i+1) != '>') {
					alert("Error : - not followed by >   Maybe trying to type -> ?");
					return false;
				} else if (s.charAt(i) === '{') {
					alert("Error : Cannot insert second left curly bracket {");
					return false;
				} else if (s.charAt(i) === '}' && rightBracketCounter === 0) {
					rightBracketCounter++;
				} else if (s.charAt(i) === '}' && rightBracketCounter === 1) {
					alert("Error : Cannot insert second right curly bracket }");
					return false;
				} else if (s.charAt(i) === '>' && s.charAt(i-1) != '-') {
					alert("Error : Illegal character '>'. Probably forgot - before > ?");
					return false;
				} else if (s.charAt(i) === '<' && (s.charAt(i+1) != '-' || s.charAt(i+2) != '>' && i+2 < s.length)) {
					alert("Error : < not followed by - and >   Maybe trying to type <-> ?");
					return false;
				} else if ((s.charAt(i) === '|' || s.charAt(i) === '&' || s.charAt(i) === '>') && ((i+1 == s.length) || ((s.charAt(i+1) === ',' || s.charAt(i+1) === ')' || s.charAt(i+1) === '}') && i+1 < s.length))) {
					alert("Error : Missing the RIGHT part in operator");
					return false;
				} else if (((s.charAt(i) === ',' || s.charAt(i) === '{' || s.charAt(i) === '~' || s.charAt(i) === '(') && (s.charAt(i+1) === '|' || s.charAt(i+1) === '&' || s.charAt(i+1) === '<' || s.charAt(i+1) === '-' || s.charAt(i+1) === '}' || s.charAt(i+1) === ',') && i+1 < s.length)) {
					alert("Error : Missing the LEFT part in operator");
					return false;
				} else if ((validCharacters.indexOf(s.charAt(i)) < 52 && s.charAt(i+1) === '(') || ((s.charAt(i) === ')' && validCharacters.indexOf(s.charAt(i+1)) < 52)) && i+1 < s.length) {
					alert("Error : Cannot have letter next to parenthesis");
					return false;
				} else if (validCharacters.indexOf(s.charAt(i)) < 52 && validCharacters.indexOf(s.charAt(i+1)) < 52 && i+1 < s.length) {
					alert("Error : Cannot have two ore more consecutive letters.");
					return false;
				} else if (validCharacters.indexOf(s.charAt(i)) === -1) {		// might be <-> or ->  								
					alert("Error : Illegal character '" + s.charAt(i) + "'.");
					return false;
				}
			}
		}
	}
}