package progetto_LFT;

import java.util.HashMap;

public class SymbolTable {
    HashMap<String, Integer> OffsetMap = new HashMap<String, Integer>();
//tengo traccia degli identificatori e dei loro indirizzi tramite una hashmap
//avendo come chiave il nome dell'identificatore, sotto forma di stringa e come valore l'indirizzo stesso
//sotto forma di integer
    public void insert(String s, int address) {
        if(!OffsetMap.containsValue(address)) {
            OffsetMap.put(s, address);
        } else {
            throw new IllegalArgumentException("Riferimento ad una locazione di memoria gia' occupata da un'altra variabile");
        }
    }

    public int lookupAddress (String s) {
        if(OffsetMap.containsKey(s))
            return OffsetMap.get(s);
        else
            return -1;
    }
}
