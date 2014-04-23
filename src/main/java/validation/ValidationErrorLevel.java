package validation;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import validation.utils.ImageLoader;
import validation.view.ModelValidationView;

/**
 * モデル検証のエラーレベルを表す列挙型です
 */
public enum ValidationErrorLevel {
    /**
     * 致命的エラーを意味します
     */
    CRITICAL(Messages.getMessage("model_validation_errorlevel.critical"), 0, 3, "critical.gif"),
    /**
     * 重大なエラーを意味します
     */
    ERROR(Messages.getMessage("model_validation_errorlevel.error"), 1, 2, "error.gif"),
    /**
     * 警告を意味します
     */
    WARNING(Messages.getMessage("model_validation_errorlevel.warning"), 2, 1, "warning.gif"),
    /**
     * デフォルト値
     * 通知レベルを意味します
     */
    NOTICE(Messages.getMessage("model_validation_errorlevel.notice"), 3, 0, "notice.gif");
    
    String label;
    int index;
    int level;
	ImageIcon imageIcon;
	String iconName;
	
    ValidationErrorLevel(String label, int index, int level, String iconName) {
        this.label = label;
        this.index = index;
        this.level = level;
        this.iconName = iconName;
        this.imageIcon = new ImageIcon(ImageLoader.getImage(ModelValidationView.class, iconName));
    }

    @Override
    public String toString() {
        return this.label;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public String getIconName(){
    	return this.iconName;
    }
    
    public Icon getIcon() {
        return this.imageIcon;
    }
}
