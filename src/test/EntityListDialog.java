/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

/**
 *
 * @author rvanya
 */
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

public class EntityListDialog {

    final JDialog dialog;
    final JTree entitiesTree;

    public EntityListDialog() {
        dialog = new JDialog((Frame) null, "Test");
        entitiesTree = createTree();
        JScrollPane entitiesTreeScrollPane = new JScrollPane(entitiesTree);
        JCheckBox pathwaysCheckBox = new JCheckBox("Do additional searches");
        JButton sendButton = new JButton("Send");
        JButton cancelButton = new JButton("Cancel");
        JButton selectAllButton = new JButton("All");
        JButton deselectAllButton = new JButton("None");

        dialog.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectPanel.add(new JLabel("Select: "));
        selectPanel.add(selectAllButton);
        selectPanel.add(deselectAllButton);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        dialog.getContentPane().add(selectPanel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 5, 0, 5);
        dialog.getContentPane().add(entitiesTreeScrollPane, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        dialog.getContentPane().add(pathwaysCheckBox, c);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(sendButton);
        buttonsPanel.add(cancelButton);
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        dialog.getContentPane().add(buttonsPanel, c);

        dialog.pack();
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        EntityListDialog dialog = new EntityListDialog();
    }

    private static JTree createTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(
            new Node("All Entities"));
        root.add(new DefaultMutableTreeNode(
            new Node("Entity 1", "Sample A", "Sample B", "Sample C")));
        root.add(new DefaultMutableTreeNode(
            new Node("Entity 2", "Sample D", "Sample E", "Sample F")));
        root.add(new DefaultMutableTreeNode(
            new Node("Entity 3", "Sample G", "Sample H", "Sample I")));
        JTree tree = new JTree(root);
        RendererDispatcher rendererDispatcher = new RendererDispatcher(tree);
        tree.setCellRenderer(rendererDispatcher);
        tree.setCellEditor(rendererDispatcher);
        tree.setEditable(true);
        return tree;
    }
}

class Node {

    final String name;
    final String[] samples;
    boolean selected;
    int selectedSampleIndex;

    public Node(String name, String... samples) {
        this.name = name;
        this.selected = false;
        this.samples = samples;
        if (samples == null) {
            this.selectedSampleIndex = -1;
        } else {
            this.selectedSampleIndex = 0;
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String toString() {
        return name;
    }

    public int getSelectedSampleIndex() {
        return selectedSampleIndex;
    }

    public void setSelectedSampleIndex(int selectedSampleIndex) {
        this.selectedSampleIndex = selectedSampleIndex;
    }

    public String[] getSamples() {
        return samples;
    }
}

interface Renderer {

    public void setForeground(final Color foreground);

    public void setBackground(final Color background);

    public void setFont(final Font font);

    public void setEnabled(final boolean enabled);

    public Component getComponent();

    public Object getContents();
}

class NodeWithSamplesRenderer implements Renderer {

    final DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
    final JPanel panel = new JPanel();
    final JCheckBox checkBox = new JCheckBox();
    final JLabel label = new JLabel("   Samples: ");
    final JComboBox comboBox = new JComboBox<String>(comboBoxModel);
    final JComponent components[] = {panel, checkBox, comboBox, label};

    public NodeWithSamplesRenderer() {
        Boolean drawFocus =
            (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
        if (drawFocus != null) {
            checkBox.setFocusPainted(drawFocus.booleanValue());
        }
        for (int i = 0; i < components.length; i++) {
            components[i].setOpaque(true);
        }

        panel.add(checkBox);
        panel.add(label);
        panel.add(comboBox);
    }

    public void setForeground(final Color foreground) {
        for (int i = 0; i < components.length; i++) {
            components[i].setForeground(foreground);
        }
    }

    public void setBackground(final Color background) {
        for (int i = 0; i < components.length; i++) {
            components[i].setBackground(background);
        }
    }

    public void setFont(final Font font) {
        for (int i = 0; i < components.length; i++) {
            components[i].setFont(font);
        }
    }

    public void setEnabled(final boolean enabled) {
        for (int i = 0; i < components.length; i++) {
            components[i].setEnabled(enabled);
        }
    }

    public void setContents(Node node) {
        checkBox.setText(node.toString());

        comboBoxModel.removeAllElements();
        for (int i = 0; i < node.getSamples().length; i++) {
            comboBoxModel.addElement(node.getSamples()[i]);
        }
    }

    public Object getContents() {
        String title = checkBox.getText();
        String[] samples = new String[comboBoxModel.getSize()];
        for (int i = 0; i < comboBoxModel.getSize(); i++) {
            samples[i] = comboBoxModel.getElementAt(i).toString();
        }
        Node node = new Node(title, samples);
        node.setSelected(checkBox.isSelected());
        node.setSelectedSampleIndex(comboBoxModel.getIndexOf(
            comboBoxModel.getSelectedItem()));
        return node;
    }

    public Component getComponent() {
        return panel;
    }
}

class NodeWithoutSamplesRenderer implements Renderer {

    final JCheckBox checkBox = new JCheckBox();

    public NodeWithoutSamplesRenderer() {
        Boolean drawFocus =
            (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
        if (drawFocus != null) {
            checkBox.setFocusPainted(drawFocus.booleanValue());
        }
    }

    public void setForeground(final Color foreground) {
        checkBox.setForeground(foreground);
    }

    public void setBackground(final Color background) {
        checkBox.setBackground(background);
    }

    public void setFont(final Font font) {
        checkBox.setFont(font);
    }

    public void setEnabled(final boolean enabled) {
        checkBox.setEnabled(enabled);
    }

    public void setContents(Node node) {
        checkBox.setText(node.toString());
    }

    public Object getContents() {
        String title = checkBox.getText();
        Node node = new Node(title);
        node.setSelected(checkBox.isSelected());
        return node;
    }

    public Component getComponent() {
        return checkBox;
    }
}

class NoNodeRenderer implements Renderer {

    final JLabel label = new JLabel();

    public void setForeground(final Color foreground) {
        label.setForeground(foreground);
    }

    public void setBackground(final Color background) {
        label.setBackground(background);
    }

    public void setFont(final Font font) {
        label.setFont(font);
    }

    public void setEnabled(final boolean enabled) {
        label.setEnabled(enabled);
    }

    public void setContents(String text) {
        label.setText(text);
    }

    public Object getContents() {
        return label.getText();
    }

    public Component getComponent() {
        return label;
    }
}

class RendererDispatcher extends AbstractCellEditor
    implements TreeCellRenderer, TreeCellEditor {

    final static Color selectionForeground =
        UIManager.getColor("Tree.selectionForeground");
    final static Color selectionBackground =
        UIManager.getColor("Tree.selectionBackground");
    final static Color textForeground =
        UIManager.getColor("Tree.textForeground");
    final static Color textBackground =
        UIManager.getColor("Tree.textBackground");
    final JTree tree;
    final NodeWithSamplesRenderer nodeWithSamplesRenderer =
        new NodeWithSamplesRenderer();
    final NodeWithoutSamplesRenderer nodeWithoutSamplesRenderer =
        new NodeWithoutSamplesRenderer();
    final NoNodeRenderer noNodeRenderer = new NoNodeRenderer();
    final Renderer[] renderers = {
        nodeWithSamplesRenderer, nodeWithoutSamplesRenderer, noNodeRenderer
    };
    Renderer renderer = null;

    public RendererDispatcher(JTree tree) {
        this.tree = tree;
        Font font = UIManager.getFont("Tree.font");
        if (font != null) {
            for (int i = 0; i < renderers.length; i++) {
                renderers[i].setFont(font);
            }
        }
    }

    public Component getTreeCellRendererComponent(JTree tree,
        Object value, boolean selected, boolean expanded,
        boolean leaf, int row, boolean hasFocus) {
        final Node node = extractNode(value);
        if (node == null) {
            renderer = noNodeRenderer;
            noNodeRenderer.setContents(tree.convertValueToText(
                value, selected, expanded, leaf, row, false));
        } else {
            if (node.getSamples() == null || node.getSamples().length == 0) {
                renderer = nodeWithoutSamplesRenderer;
                nodeWithoutSamplesRenderer.setContents(node);
            } else {
                renderer = nodeWithSamplesRenderer;
                nodeWithSamplesRenderer.setContents(node);
            }
        }

        renderer.setEnabled(tree.isEnabled());
        if (selected) {
            renderer.setForeground(selectionForeground);
            renderer.setBackground(selectionBackground);
        } else {
            renderer.setForeground(textForeground);
            renderer.setBackground(textBackground);
        }

        renderer.getComponent().repaint();
        renderer.getComponent().invalidate();
        renderer.getComponent().validate();

        return renderer.getComponent();
    }

    public Component getTreeCellEditorComponent(
        JTree tree, Object value, boolean selected,
        boolean expanded, boolean leaf, int row) {
        return getTreeCellRendererComponent(
            tree, value, true, expanded, leaf, row, true);
    }

    public Object getCellEditorValue() {
        return renderer.getContents();
    }

    public boolean isCellEditable(final EventObject event) {
        if (!(event instanceof MouseEvent)) {
            return false;
        }

        final MouseEvent mouseEvent = (MouseEvent) event;
        final TreePath path = tree.getPathForLocation(
            mouseEvent.getX(), mouseEvent.getY());
        if (path == null) {
            return false;
        }

        Object node = path.getLastPathComponent();
        if (node == null || (!(node instanceof DefaultMutableTreeNode))) {
            return false;
        }

        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
        Object userObject = treeNode.getUserObject();

        return (userObject instanceof Node);
    }

    private static Node extractNode(Object value) {
        if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if ((userObject != null) && (userObject instanceof Node)) {
                return (Node) userObject;
            }
        }

        return null;
    }
}