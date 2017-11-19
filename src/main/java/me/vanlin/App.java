package me.vanlin;

import me.vanlin.tree.rbtree.RBTree;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        RBTree<String, String> tree = new RBTree<>();

        tree.put("1", "a");
        tree.put("2", "b");
        tree.put("3", "c");
        tree.put("4", "d");
        tree.put("5", "e");

        System.out.println(tree.get("1"));
        System.out.println(tree.get("2"));
        System.out.println(tree.get("3"));
        System.out.println(tree.get("4"));
        System.out.println(tree.get("5"));

        tree.remove("3");

        System.out.println(tree.get("1"));
        System.out.println(tree.get("2"));
        System.out.println(tree.get("3"));
        System.out.println(tree.get("4"));
        System.out.println(tree.get("5"));
    }
}
