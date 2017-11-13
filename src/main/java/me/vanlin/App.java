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

        tree.put("hello", "123");
        tree.put("world", "223");

        System.out.println(tree.get("hello"));
        System.out.println(tree.get("world"));
    }
}
