package treeOfUsages.node.icon.method;

import javax.swing.Icon;

import com.intellij.icons.AllIcons;
import treeOfUsages.node.icon.HasIcon;

public class MethodIcon implements HasIcon
{
    public MethodIcon()
    {
    }

    @Override
    public Icon getIcon()
    {
        return AllIcons.Nodes.Method;
    }
}