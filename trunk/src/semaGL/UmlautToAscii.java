package semaGL;

public class UmlautToAscii {
	public UmlautToAscii () {}
	
	public static String umlautToAscii(String in) {
		String tmp =  in.toString();
		String out ="";
		String[] src = {"�","�","�","�"};
		String[] rep = {"ae","oe","ue","sz"};
		
		for (int i=0; i<src.length; i++) {
			out = tmp.replace(src[i], rep[i]);
			tmp = out;
		}
		return out;
	}
}
