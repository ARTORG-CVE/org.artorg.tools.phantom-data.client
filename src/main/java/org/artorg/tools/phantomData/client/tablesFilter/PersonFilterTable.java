package org.artorg.tools.phantomData.client.tablesFilter;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.Person;

public class PersonFilterTable extends DbUndoRedoFactoryEditFilterTable<Person> {

	{
		List<AbstractColumn<Person>> columns =
				new ArrayList<AbstractColumn<Person>>();
		
		columns.add(new FilterColumn<Person>(
				"title", item -> item.getAcademicTitle(), 
				path -> path.getPrefix(), 
				(path,value) -> {}));
		columns.add(new FilterColumn<Person>(
				"firstname", item -> item, 
				path -> path.getFirstname(), 
				(path,value) -> path.setFirstname((String) value)));
		columns.add(new FilterColumn<Person>(
				"lastname", item -> item, 
				path -> path.getLastname(), 
				(path,value) -> path.setLastname((String) value)));
		columns.add(new FilterColumn<Person>(
				"gender", item -> item.getGender(), 
				path -> path.getName(), 
				(path,value) -> path.setName((String) value)));
		setColumns(columns);
		
		setTableName("Persons");
	}

}
