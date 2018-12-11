package init;

import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public class Test {

	public static void main(String[] args) {
		ObservableMap<String, Integer> map = FXCollections.observableHashMap();

		map.put("Test", 5);

		map.addListener(new MapChangeListener<String, Integer>() {

			@Override
			public void onChanged(Change<? extends String, ? extends Integer> change) {
				System.out.println("map changed" + map.entrySet().stream()
					.map(e -> "(" + e.getKey() + ": " + e.getValue().toString() +")")
					.collect(Collectors.joining(", ", "[", "]")) +", was added: " +change.wasAdded() +", was removed: " +change.wasRemoved());
			}

		});
		
		map.put("Test", 6);
		
		map.put("Test2", 7);
		
		map.put("Test", 8);
		
		map.remove("hkjhkj");

	}

}
