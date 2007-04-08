public class RDParser {
	char[] input;
	int parsingPosition;
	boolean[] isLeftAssociative;
	boolean[] isBinary;
	boolean[] isOperator;
	int[] presedence;
	public RDParser() {
		isLeftAssociative=new boolean[256];
		isBinary=new boolean[256];
		isOperator=new boolean[256];
		presedence= new int[256];
		isOperator['+']=true;
		isOperator['-']=true;
		isOperator['*']=true;
		isOperator['/']=true;
		isOperator['^']=true;
		isOperator['&']=true;
		isOperator['|']=true;
		isOperator['~']=true;
		isLeftAssociative['+']=true;
		isLeftAssociative['-']=true;
		isLeftAssociative['*']=true;
		isLeftAssociative['/']=true;
		isLeftAssociative['^']=false;
		isLeftAssociative['&']=true;
		isLeftAssociative['|']=true;
		isLeftAssociative['~']=false;
		isBinary['+']=true;
		isBinary['-']=true;
		isBinary['*']=true;
		isBinary['/']=true;
		isBinary['^']=true;
		isBinary['&']=true;
		isBinary['|']=true;
		isBinary['~']=false;
		presedence['|']=0;
		presedence['&']=1;
		presedence['+']=2;
		presedence['-']=2;
		presedence['~']=2;
		presedence['*']=3;
		presedence['/']=3;
		presedence['^']=4;
	}

	private int Next() {
		if(parsingPosition<input.length)
			return input[parsingPosition];
		else
			return -1;
	}

	private boolean Expect(int e) {
		if(Next()==e) {
			Consume();
			return true;
			}
		else {
			System.out.println("Error: Expected '"+((char)e)+"'");
			return false;
		}
	}
	private void Consume() {
		if(parsingPosition<input.length) ++parsingPosition;
	}

	public void addVariable(String str, int value) {
	}

/*	private int symNumber(String ident,int Base) {
		int i=0;
		while( (i+parsingPosition<input.length) && (input[parsingPosition+i]>='0') &&(input[parsingPosition+i]<='9') ) {
			++i;
			}
		int n=0;
		int t=1;
		int p=parsingPosition+i;
		for(--i;i>=0; i--) {
			System.out.println(ident+"i="+i+" pp="+parsingPosition + " il="+input.length+" i+pp="+(parsingPosition+i)+" n="+n+" c="+(input[parsingPosition+i]));
			n=n+t*(input[parsingPosition+i]-'0');
			t=t*Base;
			}
		//n=n+f*t;
		parsingPosition=p;
		System.out.println(ident+"symNumber="+n+" Next()="+((char)Next())+" pp="+parsingPosition);
		return n;
	} */

	private boolean inBase(int Base, char c) {
		switch(Base) {
			case 16:
			if((c>='A')&&(c<='F')) return true;
			if((c>='a')&&(c<='f')) return true;
			case 10:
			default:
			if((c>='0')&&(c<='9')) return true;
		}
		return false;
	}

	private int StrToInt(String in) {
		String s=in.trim();
		try {
			int i=s.indexOf("$");
			if(i>-1) { //Hex
				return Integer.parseInt( s.substring(i+1), 16 );
			}
			else {
				return Integer.parseInt( s, 10 );
			}
		}
		catch ( NumberFormatException ee ) {
				System.out.println( ee.getMessage() + " is not a valid format for an integer." );
		}
		return -1;
	}


	private int symNumber(String ident, int Base) {
		int n=0;
		int i=0;
		String s="";
		System.out.println("base="+Base);
		switch(Base) {
			case 16:
				++i; //skip '$'
				while((i+parsingPosition<input.length) && inBase(16, input[parsingPosition+i])) {
					s+=input[parsingPosition+i];
					++i;
				}
				parsingPosition+=i;
				return StrToInt("$"+s);
			case 10:
			default:
				while((i+parsingPosition<input.length) && inBase(10, input[parsingPosition+i])) {
					s+=input[parsingPosition+i];
					++i;
				}
				parsingPosition+=i;
				return StrToInt(s);
		}




	/*
		int i=0;
		int n=0;
		int t=1;
		int p=parsingPosition;
		switch(Base){
			case 16:
				int q=input[parsingPosition]
				while(q>-1) {
				if
				}

				while((i+parsingPosition<input.length) && ( ((input[parsingPosition+i]>='0') &&(input[parsingPosition+i]<='9')) || ((input[parsingPosition+i]>='a')&&(input[parsingPosition+i]<='f')) || ((input[parsingPosition+i]>='A')&&(input[parsingPosition+i]<='F')) )) ++i;
				p+=i;
			for(--i;i>=0; i--) {
				System.out.println(ident+"i="+i+" pp="+parsingPosition + " il="+input.length+" i+pp="+(parsingPosition+i)+" n="+n+" c="+(input[parsingPosition+i]));
				int j=(input[parsingPosition+i]);
				n=n+t*j;
				t=t*Base;
				}

			case 10:
			case default:
				while((i+parsingPosition<input.length) && ( ((input[parsingPosition+i]>='0') &&(input[parsingPosition+i]<='9')))) ++i;
		}*/

		//return n;
	}

	private int P(String ident) {
		System.out.println(ident+"P() Next()="+((char)Next()));
		if(isOperator[Next()]&&(!isBinary[Next()])) {
			int op=Next();
			Consume();
			switch(op) {
				case '~':
					return -Expr(presedence[op],ident+"  ");
				default:
					System.out.println(ident+"Unknown unary operator '"+((char)op)+"'");
					return -1;
			}
		}
		else if(Next()=='(') {
			Consume();
			System.out.println(ident+"parsing '(' next="+((char)Next()));
			int t=Expr(0, ident+"  ");
			System.out.println(ident+"expecting ')', t="+t+" next="+((char)Next()));
			if(Expect(')'))
				return t;
			else
				return -1;
		}
		else if( (Next()>='0') && (Next()<='9')) {
			int t=symNumber(ident+"  ", 10);
			return t;
		}
		else if(Next()=='$') { //Hexnumber
			int t=symNumber(ident+"  ",16);
			return t;
		}
		else {
			System.out.println(ident+"Error: No case for P('"+((char)Next())+"')");
			return -1;
		}
	}

	private int Expr(int p, String ident) {
		System.out.println(ident+"Expr("+p+") Next()="+((char)Next()));
		int t1=P(ident+"  ");
		System.out.println(ident+"t1="+t1);
		while((Next()!=-1) &&isBinary[Next()] && presedence[Next()]>=p ) {
			int op = Next();
			System.out.println(ident+"Expr while Next()="+((char)Next())+" isleftass="+isLeftAssociative[op]);
			Consume();
			int t2=0;
			if(isLeftAssociative[op]) //Inverted?!
				t2=Expr(presedence[op], ident+"  ");
			else
				t2=Expr(presedence[op]+1, ident+"  ");
			System.out.println(ident+"t2="+t2);
			System.out.println(ident+"op="+((char)op));
			switch(op) {
				case '+':
					System.out.println(ident+"t1="+t1+" t2="+t2+" t1+t2="+(t1+t2));
					return t1+t2;
				case '-':
					System.out.println(ident+"t1="+t1+" t2="+t2+" t1-t2="+(t1-t2));
					return t1-t2;
				case '*':
					System.out.println(ident+"t1="+t1+" t2="+t2+" t1*t2="+(t1*t2));
					return t1*t2;
				case '/':
					System.out.println(ident+"t1="+t1+" t2="+t2+" t1/t2="+(t1/t2));
					return t1/t2;
				case '^':
					System.out.println(ident+"t1="+t1+" t2="+t2+" t1^t2="+(int)Math.round(Math.pow(t1, t2)));
					return (int)Math.round(Math.pow(t1, t2));
				case '&':
					System.out.println(ident+"t1="+t1+" t2="+t2+" t1&t2="+(t1&t2));
					return t1&t2;
				case '|':
					System.out.println(ident+"t1="+t1+" t2="+t2+" t1|t2="+(t1|t2));
					return t1|t2;
				default:
					System.out.println(ident+"Unknown binary operator '"+((char)op)+"'");
					return -1;
				}
		}
		return t1;
	}

	public int Evaluate(String str) {
		if(str!=null && str.length()>0) {
			input = str.toCharArray();
			int i=0;
			int j=1;
			while((j!=str.length()) && (i!=str.length()) ){
				if(input[i]==' ') {
					input[i]=input[j];
					input[j++]=' ';
					}
				else {++i;}
			}
			System.out.println(input);
			parsingPosition=0;
			return Expr(0, "");
		}
		else return -1;
	}

	public static void main( String[] args ) {
		RDParser parser=new RDParser();
		//System.out.println(parser.Evaluate("  -   2*(3+ 2^ (4-1  ) )"));
		//System.out.println(parser.Evaluate("(12+34)*5"));
		//System.out.println("--------------------------");
		parser.addVariable("A", 1);
		parser.addVariable("B", 2);
		parser.addVariable("HL", 3);
		if(args.length>0) System.out.println(parser.Evaluate(args[0]));
	}
}