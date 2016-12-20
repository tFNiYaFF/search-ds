package ru.mail.polis;

/**
 * Created by Никита on 17.12.2016.
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.jar.Pack200;

public class RedBlackTree<E extends Comparable<E>> implements ISortedSet<E> {
    private Node root;
    private int size;
    private Node NIL;
    private final Comparator<E> comparator;

    public RedBlackTree() {
        this.comparator = null;
        NIL = new Node(Color.BLACK, null,null,null,(E)new Integer(0));
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
        NIL = new Node(Color.BLACK, null,null,null,(E)new Integer(0));
    }

    private void RIGHT_ROTATE(Node y){
        Node x = y.left;
        y.left = x.right;
        if(!x.right.equals(NIL)){
            x.right.parent = y;
        }
        x.parent = y.parent;
        if(y.parent==null){
            root = x;
        }
        else{
            if(y.equals(y.parent.left)){
                y.parent.left = x;
            }
            else{
                y.parent.right = x;
            }
        }
        x.right = y;
        y.parent = x;
    }

    private void Insert_Fix(Node z){
        Node y;
        while(z.parent!=null && z.parent.color==Color.RED){ //!=null(?)
            if(z.parent==z.parent.parent.left){
                y = z.parent.parent.right;
                if(y.color==Color.RED){
                    z.parent.color = Color.BLACK;
                    y.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    z = z.parent.parent;
                }
                else{
                    if(z==z.parent.right){
                        z = z.parent;
                        LEFT_ROTATE(z);
                    }
                    z.parent.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    RIGHT_ROTATE(z.parent.parent);
                }
            }
            else{
                y = z.parent.parent.left;
                if(y.color==Color.RED){
                    z.parent.color = Color.BLACK;
                    y.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    z = z.parent.parent;
                }
                else{
                    if(z==z.parent.left){
                        z = z.parent;
                        RIGHT_ROTATE(z);
                    }
                    z.parent.color = Color.BLACK;
                    z.parent.parent.color = Color.RED;
                    LEFT_ROTATE(z.parent.parent);
                }
            }
        }
        root.color = Color.BLACK;
    }

    private void LEFT_ROTATE(Node x){
        Node y = x.right;
        x.right = y.left;
        if(!y.left.equals(NIL)){
            y.left.parent = x;
        }
        y.parent = x.parent;
        if(x.parent==null){
            root = y;
        }
        else{
            if(x.equals(x.parent.left)){
                x.parent.left = y;
            }
            else{
                x.parent.right = y;
            }
        }
        y.left = x;
        x.parent = y;
    }

    private enum Color{
        RED,
        BLACK,
    }

    private class Node{
        private Color color;
        private Node left;
        private Node right;
        private Node parent;
        private E key;

        public Node(Color color, Node left, Node right, Node parent, E key){
            this.color = color;
            this.left = left;
            this.right = right;
            this.key = key;
            this.parent = parent;
        }

        @Override
        public boolean equals(Object obj){
            Node tmp = (Node) obj;
            if(this.color == tmp.color && this.left == tmp.left && this.right==tmp.right && this.parent==tmp.parent && this.key.equals(tmp.key)){
                return true;
            }
            return false;
        }

    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no first element");
        }
        Node curr = root;
        while (!curr.left.equals(NIL)) {
            curr = curr.left;
        }
        return curr.key;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no last element");
        }
        Node curr = root;
        while (!curr.right.equals(NIL)) {
            curr = curr.right;
        }
        return curr.key;
    }

    @Override
    public List<E> inorderTraverse(){
        List<E> result = new ArrayList<E>();
        inorderTraverse(root,result);
        return result;
    }

    private void inorderTraverse(Node node, List<E> list) {
        if (node==null || node.equals(NIL)) {
            return;
        }
        inorderTraverse(node.left, list);
        list.add(node.key);
        inorderTraverse(node.right, list);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public boolean contains(E value)  {
        if(value==null) {
            throw new NullPointerException();
        }
        return FindP(value,root);
    }

    private boolean FindP(E key, Node node){
        if(node==null || node.equals(NIL)){
            return false;
        }
        if(compare(key,node.key)==0){
            return true;
        }
        if(compare(key,node.key)==-1){
            return FindP(key,node.left);
        }
        else{
            return FindP(key,node.right);
        }
    }

    @Override
    public boolean add(E value) {
        if(value==null){
            throw new NullPointerException();
        }
        if(this.contains(value)){
            return false;
        }
        if(root==null){
            root = new Node(Color.BLACK,NIL,NIL,null,value);
        }
        else {
            Insert(new Node(Color.BLACK, null, null, null, value));
        }
        size++;
        return true;
    }

    private void Insert(Node z){
        Node y = NIL;
        Node x = root;
        while(!x.equals(NIL)){
            y = x;
            if(compare(z.key,x.key)==-1){ //z.key<x.key
                x = x.left;
            }
            else{
                x = x.right;
            }
        }
        z.parent = y;
        if(y.equals(NIL)){
            root = z;
        }
        else{
            if(compare(z.key,y.key)==-1){ //z.key<y.key
                y.left = z;
            }
            else{
                y.right = z;
            }
        }
        z.left = NIL;
        z.right = NIL;
        z.color = Color.RED;
        Insert_Fix(z);
    }

    @Override
    public boolean remove(E value) {
        if(value==null){
            throw new NullPointerException();
        }
        Node r = findNode(root,value);
        if(r==null){
            return false;
        }
        else{
            if(size==1){
                root = null;
            }
            else {
                DeleteP(r);
            }
        }
        size--;
        return true;
    }

    private Node findNode(Node node, E key){
        if(node==null || node.equals(NIL)){
            return null;
        }
        if(compare(node.key, key)==0){
            return node;
        }
        if(compare(node.key, key)==1){ //node.key>key
            return findNode(node.left,key);
        }
        else{
            return findNode(node.right, key);
        }
    }

    private Node minimum(Node x){
        while(!x.left.equals(NIL)){
            x = x.left;
        }
        return x;
    }

    private Node successor(Node x){
        Node y;
        if(!x.right.equals(NIL)){
            return minimum(x.right);
        }
        y = x.parent;
        while(!y.equals(NIL) && x.equals(y.right)){
            x =y;
            y = y.parent;
        }
        return y;
    }

    private void DeleteP(Node z){
        Node x,y;
        if(z.left.equals(NIL) || z.right.equals(NIL)){
            y = z;
        }
        else{
            y = successor(z);
        }
        if(!y.left.equals(NIL)){
            x = y.left;
        }
        else{
            x = y.right;
        }
        x.parent = y.parent;
        if(y.parent==null){
            root = x;
        }
        else{
            if(y.equals(y.parent.left)){
                y.parent.left = x;
            }
            else{
                y.parent.right = x;
            }
        }
        if(!y.equals(z)){
            z.key = y.key;
        }
        if(y.color==Color.BLACK){
            Fix_Delete(x);
        }
    }

    private void Fix_Delete(Node x){
        Node w;
        while(!x.equals(root) && x.color==Color.BLACK){
            if(x.equals(x.parent.left)){
                w = x.parent.right;
                if(w.color==Color.RED){
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    LEFT_ROTATE(x.parent);
                    w = x.parent.right;
                }

                if(w.left.color==Color.BLACK && w.right.color==Color.BLACK){
                    w.color = Color.RED;
                    x = x.parent;
                }
                else{
                    if(w.right.color==Color.BLACK){
                        w.left.color = Color.BLACK;
                        w.color = Color.RED;
                        RIGHT_ROTATE(w);
                        w = x.parent.right;
                    }
                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.right.color = Color.BLACK;
                    LEFT_ROTATE(x.parent);
                    x = root;
                }
            }
            else{
                w = x.parent.left;
                if(w.color==Color.RED){
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    RIGHT_ROTATE(x.parent);
                    w = x.parent.left;
                }

                if(w.right.color==Color.BLACK && w.left.color==Color.BLACK){
                    w.color = Color.RED;
                    x = x.parent;
                }
                else{
                    if(w.left.color==Color.BLACK){
                        w.right.color = Color.BLACK;
                        w.color = Color.RED;
                        LEFT_ROTATE(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.left.color = Color.BLACK;
                    RIGHT_ROTATE(x.parent);
                    x = root;
                }
            }
        }
        x.color = Color.BLACK;
    }
    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<Integer>();
        tree.add(1);
        tree.add(2);
        tree.remove(1);
        tree.remove(2);
        tree.add(0);
        tree.remove(0);
        System.out.println(tree.inorderTraverse().toString());
    }
}
