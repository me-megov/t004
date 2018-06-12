/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.megov.emc.t004.augmentedtree;

import me.megov.emc.t004.entities.IPvXTuple;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author megov
 * @param <T>
 */

public class AUTree<T extends Object> {

    private AUTNode<T> root;

    private List<AUTNode<T>> containsTupleRecursive(AUTNode<T> current, AUTNode<T> nodeToFind, List<AUTNode<T>> alreadyFound) {

        if (current == null) {
            return alreadyFound;
        }

        if (!((current.bounds.getLowerBound().compareTo(nodeToFind.bounds.getUpperBound()) > 0)
           || (current.bounds.getUpperBound().compareTo(nodeToFind.bounds.getLowerBound()) < 0))) {
            alreadyFound.add(current);
        }

        if ((current.left != null)
        && (current.left.maxRight.compareTo(nodeToFind.bounds.getLowerBound()) >= 0)) {
            this.containsTupleRecursive(current.left, nodeToFind, alreadyFound);
        }
        this.containsTupleRecursive(current.right, nodeToFind, alreadyFound);
        return alreadyFound;
    }

    public List<AUTNode<T>> containsTuple(IPvXTuple value) {
        return containsTupleRecursive(root, new AUTNode<>(value, value, null), new ArrayList<>());
    }

    public AUTNode<T> addNode(AUTNode<T> newNode) {
        root = addNodeRecursive(root, newNode);
        return root;
    }

    public AUTNode<T> addNodeRecursive(AUTNode<T> current, AUTNode<T> newNode) {
        if (current == null) {
            return newNode;
        }

        if (newNode.bounds.getUpperBound().compareTo(current.maxRight) > 0) {
            current.maxRight = new IPvXTuple(newNode.bounds.getUpperBound());
        }

        if (current.compareTo(newNode) <= 0) {

            if (current.right == null) {
                current.right = newNode;
            } else {
                addNodeRecursive(current.right, newNode);
            }
        } else if (current.left == null) {
            current.left = newNode;
        } else {
            addNodeRecursive(current.left, newNode);
        }
        return current;
    }

    private void dumpRecursive(AUTNode node, int _level, PrintStream _ps) {
        String tab = "";
        if (_level > 0) {
            tab = new String(new char[_level]).replace('\0', '.');
        }
        if (node == null) {
            return;
        }

        if (node.left == null) {
            _ps.println(tab + ":" + "L=NULL");
        } else {
            dumpRecursive(node.left, _level + 1, _ps);
        }

        _ps.println(tab + "->" + node.toString());

        if (node.right == null) {
            _ps.println(tab + ":" + "R=NULL");
        } else {
            dumpRecursive(node.right, _level + 1, _ps);
        }
    }

    public void dump(PrintStream _ps) {
        dumpRecursive(root, 0, _ps);
    }

}
