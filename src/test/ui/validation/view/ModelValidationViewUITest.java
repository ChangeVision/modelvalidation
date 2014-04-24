package validation.view;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.table.TableColumnModel;

import org.fest.swing.data.TableCell;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.junit.v4_5.runner.GUITestRunner;
import org.fest.swing.timing.Pause;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import validation.DefaultValidationRule;
import validation.Messages;
import validation.QuickFix;
import validation.ValidationError;
import validation.ValidationRule;
import validation.ValidationRuleManager;
import validation.view.ModelValidationView.UpdateButton;

import com.change_vision.jude.api.inf.model.IAssociation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;

@RunWith(GUITestRunner.class)
public class ModelValidationViewUITest {
	private ModelValidationView view;
	private FrameFixture frameFixture;
	private JTableFixture tableFixture;
	private JPanelFixture panelFixture;

	@Before
	public void setUp() throws Exception {

		view = new ModelValidationView();

		JFrame frame = new JFrame("ModelValidationViewTest");
		frame.add(view);

		frameFixture = new FrameFixture(frame);
		panelFixture = new JPanelFixture(frameFixture.robot, view);
		frameFixture.moveToFront();
		frameFixture.show();
		frameFixture.focus();

		tableFixture = frameFixture.table(ModelValidationTable.NAME);
	}

	@After
	public void tearDown() throws Exception {
		if (frameFixture != null) {
			frameFixture.cleanUp();
		}
		view.errorClear();
		ModelValidationViewLocator.getInstance().clearRuleManager();
	}

	@Test
	public void テーブルヘッダーには_カテゴリ_説明_要素の種類_要素のパス_エラーレベルが表示されること() throws Exception {
		tableFixture.requireColumnCount(6);

		TableColumnModel header = tableFixture.target.getColumnModel();

		assertThat(header.getColumn(0).getHeaderValue().toString(),
				is(Messages.getMessage("model_validation_table.category")));
		assertThat(header.getColumn(1).getHeaderValue().toString(),
				is(Messages.getMessage("model_validation_table.description")));
		assertThat(header.getColumn(2).getHeaderValue().toString(),
				is(Messages.getMessage("model_validation_table.kind")));
		assertThat(header.getColumn(3).getHeaderValue().toString(),
				is(Messages.getMessage("model_validation_table.model")));
		assertThat(header.getColumn(4).getHeaderValue().toString(),
				is(Messages.getMessage("model_validation_table.path")));
		assertThat(header.getColumn(5).getHeaderValue().toString(),
				is(Messages.getMessage("model_validation_table.errorlevel")));
	}

	@Test
	public void エラー件数分_テーブルの行数が作成されること() throws Exception {
		ValidationRuleManagerImpl ruleManager = new ValidationRuleManagerImpl() {

			@Override
			public void buildMockModels() {
				targets.add(mock(INamedElement.class));
				targets.add(mock(INamedElement.class));
				targets.add(mock(INamedElement.class));
				targets.add(mock(INamedElement.class));
			}
		};
		setupTable(ruleManager);

		tableFixture.requireRowCount(4);
	}

	@Test
	public void エラー要素の_要素の種類_要素のパスが正しく表示されること_Block() throws Exception {
		ValidationRuleManagerImpl ruleManager = new ValidationRuleManagerImpl() {

			@Override
			public void buildMockModels() {
				IBlock block = mock(IBlock.class);
				when(block.getName()).thenReturn("Block0");
				when(block.getFullNamespace("/")).thenReturn("com/sample");
				targets.add(block);
			}
		};
		setupTable(ruleManager);

		assertThat(tableFixture.cell(TableCell.row(0).column(2)).value(),
				is(Messages.getMessage("model_validation_type.block")));
		assertThat(tableFixture.cell(TableCell.row(0).column(4)).value(), is("/com/sample"));
		assertThat(getColumnIcon(0,4),is(isNotNull()));
	}

	@Test
	public void エラー要素の_要素の種類_要素のパスが正しく表示されること_Part() throws Exception {
		ValidationRuleManagerImpl ruleManager = new ValidationRuleManagerImpl() {

			@Override
			public void buildMockModels() {
				IAttribute firstEnd = mock(IAttribute.class);
				IAttribute secondEnd = mock(IAttribute.class);
				IBlock block = mock(IBlock.class);
				
				when(firstEnd.getType()).thenReturn(block);
				when(secondEnd.isComposite()).thenReturn(true);
				IAssociation association = mock(IAssociation.class);
				
				IAttribute[] attributes = new IAttribute[]{
						firstEnd,
						secondEnd
				};
				when(association.getMemberEnds()).thenReturn(attributes);
				when(firstEnd.getAssociation()).thenReturn(association);
				
				when(firstEnd.getName()).thenReturn("partA");
				when(firstEnd.getFullNamespace("/")).thenReturn("partA");
				targets.add(firstEnd);
			}
		};
		setupTable(ruleManager);

		assertThat(tableFixture.cell(TableCell.row(0).column(2)).value(),
				is(Messages.getMessage("model_validation_type.part")));
		assertThat(tableFixture.cell(TableCell.row(0).column(4)).value(), is("/partA"));
		assertThat(getColumnIcon(0,4),is(isNotNull()));
	}

	@Test
	public void エラー要素の_要素の種類_要素のパスが正しく表示されること_Port() throws Exception {
		ValidationRuleManagerImpl ruleManager = new ValidationRuleManagerImpl() {

			@Override
			public void buildMockModels() {
				IPort port = mock(IPort.class);
				when(port.getName()).thenReturn("portA");
				when(port.getFullNamespace("/")).thenReturn("com/sample");
				targets.add(port);
			}
		};
		setupTable(ruleManager);

		assertThat(tableFixture.cell(TableCell.row(0).column(2)).value(),
				is(Messages.getMessage("model_validation_type.port")));
		assertThat(tableFixture.cell(TableCell.row(0).column(4)).value(), is("/com/sample"));
		assertThat(getColumnIcon(0,4),is(isNotNull()));
	}
	
	private Icon getColumnIcon(int row,int column){
		Component c = tableFixture.target.prepareRenderer(tableFixture.target.getCellRenderer(row, column), row, column);
		if(c instanceof JLabel){
			return ((JLabel)c).getIcon();
		}
		return null;
	}
	private void setupTable(ValidationRuleManagerImpl ruleManager) {
		ModelValidationViewLocator.getInstance().addValidationRuleManager(ruleManager);

		panelFixture.button(UpdateButton.NAME).click();

		while (!view.isValidationDone()) {
			Pause.pause();
		}
	}

	private static abstract class ValidationRuleManagerImpl implements ValidationRuleManager {

		public List<INamedElement> targets = new ArrayList<INamedElement>();

		@Override
		public List<ValidationRule> getValidationRule() {
			DefaultValidationRule rule = new DefaultValidationRule() {
				int count = 0;

				@Override
				public boolean validate(INamedElement target) {
					setResult(new ValidationError("category","test" + count, target, this));
					count++;
					return false;
				}

				@Override
				public boolean isTargetModel(INamedElement target) {
					return true;
				}

				@Override
				public List<QuickFix> getQuickFixes(INamedElement target) {
					return null;
				}
			};

			List<ValidationRule> rules = new ArrayList<ValidationRule>();
			rules.add(rule);
			return rules;
		}

		@Override
		public List<INamedElement> getTargetModels() {
			targets = new ArrayList<INamedElement>();
			buildMockModels();
			return targets;
		}

		public abstract void buildMockModels();

	}
}
