package myToolWindow.Nodes.Icons.ClassNodes;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import myToolWindow.Nodes.Icons.HasIcon;

public class MethodNode implements HasIcon
{
    public MethodNode()
    {
    }

    @Override
    public Icon getIcon()
    {
        return AllIcons.Nodes.Method;
    }
}