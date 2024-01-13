// INSERIRE I PROPRI DATI PERSONALI
// nome e cognome del candidato, matricola, data,       numero postazione
// Giulio Berton  2106260   09/01/2024  NaN

import java.util.Scanner;
import java.util.NoSuchElementException;
import java.io.*;
// -------------- classe TranslatorTester: da completare -----------------
public class TranslatorTester
{   public static void main(String[] args)
    {
    	if(args.length != 1)
    	{
    		System.out.println("Il programma riceve un file di testo da riga di comando");
    		System.exit(1);
    	}
    	Scanner fileScan = null;
    	try
    	{
    		fileScan = new Scanner(new FileReader(args[0]));
    	}
    	catch(FileNotFoundException e)
    	{
    		System.out.println("Errore nell'apertura del file");
    		System.exit(1);
    	}
    	StringMap traduttore = new Translator(fileScan);
    	boolean done = false;
    	Scanner console = new Scanner(System.in);
    	while(!done)
    	{
    		System.out.println("******************************************" + "\n");
    		System.out.println("Inserire un comando tra: " + "\n" + "P [Print] Stampa a standard output tutto il dizionario inglese-italiano. \nF [Find] Cerca una parola inglese.\nFA [Find All] Cerca tutte le parole che iniziano con una determinata sottostringa, ovvero un prefisso.\nQ [Quit] Termina il programma.");
    		System.out.println("******************************************");
    		String comando = console.nextLine();
    		System.out.println(comando + "\n");
    		if(comando.equalsIgnoreCase("P"))
    		{
    			System.out.println(traduttore);
    		}
    		else if(comando.equalsIgnoreCase("F"))
    		{
    			System.out.println("Inserire su una nuova riga la parola da cercare");
    			String toFind = console.nextLine();
    			String[] traduzioni = null;
    			String s = "";
    			try
    			{
    				traduzioni = (String[]) traduttore.find(toFind);
    				for(int i = 0; i < traduzioni.length; i++)
    				{
    					s = s + traduzioni[i] + ", ";
    				}
    				s = s.substring(0, s.length()-2);
    				System.out.println(s);
    			}
    			catch(MapItemNotFoundException e)
    			{
    				System.out.println("La parola inserita non e' presente nel dizionario");
    			}
    		}
    		else if(comando.equalsIgnoreCase("FA"))
    		{
    			System.out.println("Inserire un prefisso da cercare");
    			String prefix = console.nextLine();
    			String[] keys = null;
    			try
    			{
    				keys = (String[])traduttore.findStartsWith(prefix); 
    				for(int i = 0; i < keys.length; i++)
    				{
    					System.out.println(keys[i]);
    					String[] traduzioni = (String[])traduttore.find(keys[i]);
    					for(int j = 0; j < traduzioni.length; j++)
    					{
    						System.out.println("\t" + traduzioni[j]);
    					}
    				}
    			}
    			catch(MapItemNotFoundException e)
    			{
    				System.out.println("Il prefisso inserito non e' presente nel dizionario");
    			}
    		}
    		else if(comando.equalsIgnoreCase("Q"))
    		{
    			System.out.println("Chiusura applicazione");
    			done = true;
    		}
    		else
    		{
    			System.out.println("Comando errato");
    		}
    	}
    }
}


// -------------------- classe Translator: da completare -------------------


/*
    Classe che implementa l'interfaccia StringMap
    La classe contiene coppie di tipo "parola, traduzioni": una possibile 
    realizzazione di queste coppie e` data dalla classe WordPair, classe 
    interna di Translator. In ogni caso il campo valore delle coppie inserite 
    in contenitori di tipo Translator e` di tipo String[] (che e` comunque
    una sottoclasse di Object ...)
    Si presti particolare attenzione al metodo findStartsWith.
    - SUGGERIMENTO 1: studiando la documentazione della classe String e` 
        possibile trovare un modo per verificare se una stringa ha un'altra
        stringa come prefisso.
    - SUGGERIMENTO 2: una volta capito come verificare se una chiave ne ha
        un'altra come prefisso, e` abbastanza facile scrivere una realizzazione
        non-ottima del metodo findStartsWith (ovvero una realizzazione corretta
        con prestazioni O(n) ). Invece scrivere una realizzazione ottima (con 
        prestazioni O(log n) ) e` nettamente piu` complicato: si consiglia di
        provarci solo dopo avere completato una realizzazione non-ottima.
*/


class Translator implements StringMap
{
    //campi di esemplare
    //costruttori e metodi pubblici di StringMap
    private WordPair[] v;
    private int vSize;

	public Translator()
	{
		v = new WordPair[10];
    	vSize = 0;
	}
    //costruttore .....  da completare secondo specifiche del compito ......
    public Translator(Scanner file)
    {
    	v = new WordPair[10];
    	vSize = 0;
    	while(file.hasNextLine())
    	{
    		String line = file.nextLine();
    		String[] words = line.split(" :,");
    		String key = words[0];
    		String[] translations = new String[words.length-1];
    		for(int i = 1, j = 0; i < words.length; i++, j++)
    		{
    			translations[j] = words[i];
    		}
    		this.insert(key, translations);
    	}
    	file.close();
    }
    
    public boolean isEmpty()
    {
    	return vSize == 0;
    } // true: contenitore vuoto; false: contenitore non vuoto

    public int size()
    {
    	return vSize;
    }       // restituisce il n. di elementi presenti nel contenitore

    // L'inserimento va sempre a buon fine; se la chiave non esiste, la coppia 
    // key/value viene aggiunta alla mappa; se la chiave esiste gia`, il valore
    // ad essa associato viene sovrascritto con il nuovo valore.
    public void insert(String key, Object value)
    {
    	if(vSize == v.length)
    	{
    		v = resize(v, vSize * 2);
    	}
    	try
    	{
    		remove(key);
    	}
    	catch(MapItemNotFoundException e)
    	{}
    	int j;
    	for(j = vSize; j > 0 && key.compareTo(v[j - 1].getWord()) < 0; j--)
    	{
    		v[j] = v[j - 1];
    	}
    	v[j] = new WordPair(key, (String[]) value);
    	vSize++;
    }

    // La rimozione della chiave rimuove anche la corrispondente coppia.
    // Lancia MapItemNotFoundException se la chiave non esiste.
    public void remove(String key)
    {
    	int index = binSearch(v, vSize, key);
    	for(int i = index; i < vSize - 1; i++)
    	{
    		v[i] = v[i + 1];
    	}
    	vSize--;
    }

    // La ricerca per chiave restituisce soltanto il valore ad essa associato
    // nella mappa. Lancia MapItemNotFoundException se la chiave non esiste.
    public Object find(String key)
    {
    	return v[binSearch(v, vSize, key)].getTranslations();
    }

    // La ricerca per "prefisso" cerca nella mappa tutte le chiavi che iniziano
    // con la stringa prefix, e restituisce tali chiavi sotto forma di un array  
    // di stringhe (pieno).
    // Lancia MapItemNotFoundException se la mappa non contiene nessuna chiave 
    // che inizia con la stringa prefix.
    public String[] findStartsWith(String prefix)
    {
    	int first = findFirst(v, prefix);
    	int last = findLast(v, prefix);
    	String[] result = new String[last-first+1];
    	for(int i = 0; i < result.length; i++)
    	{
    		result[i] = v[first+i].getWord();
    	}
    	return result;
    }
    private int findFirst(WordPair[] v , String prefix)
    {
    	int found = prefixBinSearch(v, vSize, prefix);
    	int first = found;
    	while(v[first - 1].getWord().startsWith(prefix) && first > 0)
    	{
    		first--;
    	}
    	return first;
    }
    private int findLast(WordPair[] v, String prefix)
    {
    	int found = prefixBinSearch(v, vSize, prefix);
    	int last = found;
    	while(v[last + 1].getWord().startsWith(prefix) && last < vSize)
    	{
    		last++;
    	}
    	return last;
    }
    private int prefixBinSearch(WordPair[] v, int vSize, String prefix)
    {
    	int from = 0;
    	int to = vSize - 1;
    	while(from <= to)
    	{
    		int mid = (from + to) / 2;
    		if(v[mid].getWord().startsWith(prefix))
    		{
    			return mid;
    		}
    		else
    		{
    			if(prefix.compareTo(v[mid].getWord()) < 0)
    			{
    				to = mid - 1;
    			}
    			else
    			{
    				if(prefix.compareTo(v[mid].getWord()) > 0)
    				{
    					from = mid + 1;
    				}
    			}
    		}
    	}
   		throw new MapItemNotFoundException();
    }

    //metodo toString ..... da completare secondo specifiche del compito ......
    public String toString()
    {
        String s = "";
        for(int i = 0; i < vSize; i++)
        {
        	s = s + v[i].toString() + "\n";
        }
        return s;
    }
    
    private WordPair[] resize(WordPair[] v, int newSize)
    {
    	WordPair[] newV = new WordPair[newSize];
    	System.arraycopy(v, 0, newV, 0, v.length);
    	return newV;
    }
    
    private int binSearch(WordPair[] v, int vSize, String word)
    {
    	int from = 0;
    	int to = vSize - 1;
    	while(from <= to)
    	{
    		int mid = (from + to) / 2;
    		if(word.equals(v[mid].getWord()))
    		{
    			return mid;
    		}
    		else
    		{
    			if(word.compareTo(v[mid].getWord()) < 0)
    			{
    				to = mid - 1;
    			}
    			else
    			{
    				if(word.compareTo(v[mid].getWord()) > 0)
    				{
    					from = mid + 1;
    				}
    			}
    		}
    	}
    	if(from > to)
    	{
    		throw new MapItemNotFoundException();
    	}
    	return 0;
    }
    
    // --------- classe interna privata WordPair: non modificare!! ---------
    private class WordPair
    {   public WordPair(String word, String[] translations)
        {   this.word = word; 
            this.translations = translations;
        }
        public String getWord() 
        { return word; }
        public String[] getTranslations() 
        { return translations; }

        //  Restituisce una stringa nel formato
        //      word : traduzione1, traduzione2, traduzione3, ecc.
        public String toString() 
        {   String retString = word + " :";
            for (int i = 0; i < translations.length; i++)
	            retString += " " + translations[i] + ",";
            return retString.substring(0,retString.length()-1);
        }
        //campi di esemplare
        private String word;           // parola inglese
        private String[] translations; // array contenente una o piu` possibili
                                       // traduzioni in italiano
    }
}



// -------------- Interfaccia StringMap: non modificare !!---------------

interface StringMap // Definisce una mappa le cui chiavi sono stringhe
{
    boolean isEmpty(); // true: contenitore vuoto; false: contenitore non vuoto

    int size();       // restituisce il n. di elementi presenti nel contenitore

    // L'inserimento va sempre a buon fine; se la chiave non esiste, la coppia 
    // key/value viene aggiunta alla mappa; se la chiave esiste gia`, il valore
    // ad essa associato viene sovrascritto con il nuovo valore.
    void insert(String key, Object value);

    // La rimozione della chiave rimuove anche la corrispondente coppia.
    // Lancia MapItemNotFoundException se la chiave non esiste.
    void remove(String key);

    // La ricerca per chiave restituisce soltanto il valore ad essa associato
    // nella mappa. Lancia MapItemNotFoundException se la chiave non esiste.
    Object find(String key);

    // La ricerca per "prefisso" cerca nella mappa tutte le chiavi che iniziano
    // con la stringa prefix, e restituisce tali chiavi sotto forma di un array  
    // di stringhe (pieno).
    // Lancia MapItemNotFoundException se la mappa non contiene nessuna chiave 
    // che inizia con la stringa prefix.
    String[] findStartsWith(String prefix);

}

//Eccezione che segnala il mancato ritrovamento di una chiave
class MapItemNotFoundException extends RuntimeException {  }


