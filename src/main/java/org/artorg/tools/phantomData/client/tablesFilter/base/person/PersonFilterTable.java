package org.artorg.tools.phantomData.client.tablesFilter.base.person;

import java.util.ArrayList;
import java.util.List;

import org.artorg.tools.phantomData.client.table.AbstractColumn;
import org.artorg.tools.phantomData.client.table.DbUndoRedoFactoryEditFilterTable;
import org.artorg.tools.phantomData.client.table.FilterColumn;
import org.artorg.tools.phantomData.server.model.base.person.Person;

public class PersonFilterTable extends DbUndoRedoFactoryEditFilterTable<Person> {

	{
		setTableName("Persons");

		setColumnCreator(items -> {
			List<AbstractColumn<Person>> columns =
				new ArrayList<AbstractColumn<Person>>();

			columns.add(new FilterColumn<Person>(
				"Title", item -> item.getAcademicTitle(),
				path -> path.getPrefix(),
				(path, value) -> {
				}));
			columns.add(new FilterColumn<Person>(
				"Firstname", item -> item,
				path -> path.getFirstname(),
				(path, value) -> path.setFirstname((String) value)));
			columns.add(new FilterColumn<Person>(
				"Lastname", item -> item,
				path -> path.getLastname(),
				(path, value) -> path.setLastname((String) value)));
			columns.add(new FilterColumn<Person>(
				"Gender", item -> item.getGender(),
				path -> path.getName(),
				(path, value) -> path.setName((String) value)));
			return columns;
		});

	}

}