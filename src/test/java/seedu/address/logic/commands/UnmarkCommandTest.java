package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Tutorial;

public class UnmarkCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_invalidIndex_throwsParseException() {
        assertThrows(ParseException.class, () ->
                new UnmarkCommand(ParserUtil.parseIndex("-1"),
                        new Tutorial("2")).execute(model));
    }

    /**
     * Unmark a person using index outside the displayed list.
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;

        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        UnmarkCommand unmarkCommand = new UnmarkCommand(outOfBoundIndex,
                new Tutorial("1"));

        assertCommandFailure(unmarkCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Unmark a person using tutorial that is already unmarked.
     */
    @Test
    public void execute_invalidTutorialNumber_failure() {
        UnmarkCommand unmarkCommand = new UnmarkCommand(INDEX_FIRST_PERSON,
                new Tutorial("1"));
        try {
            unmarkCommand.execute(model);
            unmarkCommand.execute(model);
        } catch (CommandException e) {
            assertCommandFailure(unmarkCommand, model,
                    String.format(UnmarkCommand.MESSAGE_UNMARK_UNNECESSARY, 1,
                            Messages.format(model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()))));
        }
    }

    @Test
    public void execute_success() {
        Tutorial tutorialToBeAdded = new Tutorial("1");

        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        UnmarkCommand unmarkCommand = new UnmarkCommand(INDEX_FIRST_PERSON, tutorialToBeAdded);

        try {
            unmarkCommand.execute(model);
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }

        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Check person is edited
        Map<Tutorial, Boolean> newTutorials = new HashMap<>(personToEdit.getTutorials());
        newTutorials.put(tutorialToBeAdded, false);
        Person expectedEditedPerson = new Person(
                personToEdit.getName(),
                personToEdit.getStudentId(),
                personToEdit.getPhone(),
                personToEdit.getEmail(),
                personToEdit.getTags(),
                newTutorials
        );
        assertEquals(expectedEditedPerson, editedPerson);

        // Check model is updated with new person attribute
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        String expectedMessage = String.format(UnmarkCommand.MESSAGE_UNMARK_SUCCESS, tutorialToBeAdded.tutorial,
                Messages.format(editedPerson));
        Model typicalModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertCommandSuccess(unmarkCommand, typicalModel, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        Tutorial tutorial = new Tutorial("1");
        UnmarkCommand unmarkCommand = new UnmarkCommand(INDEX_FIRST_PERSON, tutorial);

        // is itself
        assertTrue(unmarkCommand.equals(unmarkCommand));

        // is null
        assertFalse(unmarkCommand.equals(null));

        // duplicate UnmarkCommand
        Tutorial duplicateTutorial = new Tutorial("1");
        UnmarkCommand duplicateUnmarkCommand = new UnmarkCommand(INDEX_FIRST_PERSON, duplicateTutorial);

        assertTrue(unmarkCommand
                .equals(duplicateUnmarkCommand));
    }
}