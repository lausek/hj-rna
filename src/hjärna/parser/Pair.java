package hjärna.parser;

public class Pair<K, V> {
	public K key;
	public V value;
	
	public Pair(K key) {
		this(key, null);
	}
	
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s)", key, value);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Pair) {
			Pair<K, V> o = (Pair<K, V>) other;
			return this.key == o.key && 
					(this.value == null && o.value == null || this.value.equals(o.value));
		}
		// TODO Auto-generated method stub
		return super.equals(other);
	}
	
}