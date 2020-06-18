import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.NoSuchElementException;

public class BST<Key extends Comparable<Key>, Value> {

    private Node root;

    // задаем узел
    private class Node {
        private Key key;  // ключ
        private Value val; // значение
        private Node right; // reference to small child
        private Node left; // reference to larger child
        private int count; // количество Node in subtree

        public Node(Key key, Value val, int count) {
            this.key = key;
            this.val = val;
            this.count = count;
        }
    }

    //value paired with key (null if key is absent)
    public Value get(Key key) {
        Node x = root;
        while (x != null) {
            //сравниваем ключи
            int cmp = key.compareTo(x.key);
            if          (cmp < 0) x = x.left;
            else if     (cmp > 0) x = x.right;
            else return x.val;
        }
        return null;
    }

    // содержит ли дерево этот ключ?
    public boolean contains(Key key) {
        return get(key) != null ;
    }

    //put key-value pair into yhe table
    public void put(Key key, Value val) {
        root = put(root, key, val);
    }
    //рекурсия
    private Node put(Node x, Key key, Value val) {
        if ( x == null) return new Node(key, val, 1);
        //сравниваем ключи
        int cmp = key.compareTo(x.key);
        if      ( cmp < 0) x.left = put(x.left, key, val); //идем влево
        else if ( cmp > 0) x.right = put(x.right, key ,val); //идем впарво
        else               x.val = val;

        x.count = 1 + size(x.left) + size(x.right); // обновляем subtree count

        return x; // возвращаемся к root
    }

    // remove key and its value from table
    public void delete(Key key) {
        root = delete(root, key);
    }
    private Node delete(Node x, Key key) {
        if ( x == null ) return null;
        //сравниваем ключи, чтобы дорбраться до нужного
        int cmp = key.compareTo(x.key);
        if      (cmp < 0) x.left = delete(x.left, key); //idem vlevo
        else if (cmp > 0) x.right = delete(x.right, key); //idem vpravo
        else { //nashli
            //if child 0 or 1
            if (x.right == null) return x.left;
            if (x.left == null) return x.right;
            //if 2 children
            Node t = x; //zapominaem
            x = min(t.right); //preemnik - naimen'shiy sprava
            x.right = deleteMin(t.right); //return subtree, but without min
            x.left = t.left; // выставляем левый узел, каким он был раньше
        }

        x.count = 1 + size(x.right) + size(x.left); // обновляем счетчик узлов

        return x; //возвращаемся к root
    }

    // дерево пустое?
    public boolean isEmpty() {
        return root == null;
    }

    // количество узлов
    public int size() {
        return size(root);
    }
    private int size(Node x) {
        if (x == null) return 0; //ok to call if is null
        return x.count;
    }

    //возвращает минимальный ключ
    public Key min() {
        if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
        Node x = min(root);
        return x.key;
    }
    private Node min(Node x) {
        if (x.left == null) return x;
        x = min(x.left);
        return x;
    }


    public Key max() {
        if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
        Node x = max(root);
        return x.key;
    }
    private Node max(Node x) {
        if ( x.right == null) return x;
        x = max(x.right);
        return x;
    }

    //largest kev <= given key
    public Key floor(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to floor() is null");
        if (isEmpty()) throw new NoSuchElementException("calls floor() with empty symbol table");
        Node x = floor(root, key); // начинаем осмотр с корня
        if (x == null) throw new NoSuchElementException("argument to floor() is too small"); // если нет узла, то нет и ключа
        return x.key; // если есть, то возвращаем его ключ
    }
    private Node floor(Node x, Key key) {
        if (x == null) return null;
        //сравниваем ключи, чтобы найти нужный узел
        int cmp = key.compareTo(x.key);
        // ключ нашелся и равен
        if   (cmp == 0) return x; // возвращаем этот узел
        // ключ меньше
        if   (cmp < 0) return floor(x.left, key); //продолжаем искать слева
        //ключ оказался больше, теперь двигаемся вниз вправо
        Node t = floor(x.right, key); // ищем справа
        if (t != null) return t; // если есть, то возвращаем его
        else return x; // если нет, то возвращаем тот, с которого свернули вправо
    }

    // smallest key >= given key
    public Key ceiling(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to floor() is null");
        if (isEmpty()) throw new NoSuchElementException("calls floor() with empty symbol table");
        Node x = ceiling(root, key); // начинаем осмотр с корня
        if (x == null) throw new NoSuchElementException("argument to floor() is too big"); // если нет узла, то нет и ключа
        return x.key; // если есть, то возвращаем его ключ
    }
    private Node ceiling(Node x, Key key) {
        if (x == null) return null;
        //сравниваем ключи, чтобы найти нужный узел
        int cmp = key.compareTo(x.key);
        //ключ равен
        if (cmp == 0) return x; // возвращаем узел, который его содержит
        //ключ больше
        if (cmp > 0) return ceiling(x.right, key);  //продолжаем искать справа
        //ключ оказался меньше, сворачиваем влево от узла x
        Node t = ceiling(x.left, key); //ищем слева от поворотного узла
        if (t != null) return t; // если такой узел нашелся, то возвращаем его
        else return x; // иначе возвращаем поворотный узел
    }

    // how many keys < given key ?
    public int rank(Key key) {
        return rank(key,root); //начнем с корня
    }
    private int rank(Key key, Node x){
        if (x == null) return 0;
        // сравниваем ключи
        int cmp = key.compareTo(x.key);

        if (cmp < 0) return rank(key, x.left); //продолжаем смотреть влево по уменьшению
        // ищем вправо +возвращаем количестов тех, что слева +1 за сам узел
        else if (cmp > 0) return rank(key, x.right) + 1 + size(x.left);
        else  return size(x.left);
    }

    //public Key select(Key key){}

    // удалить наименьшее значение
    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("Symbol table underflow");
        root = deleteMin(root); // придется исправлять все count начиная с низа до самого верха
    }
    private Node deleteMin(Node x) {
        if (x.left == null) return x.right; // заменяем на правый узел, если он null, то весь узел будет null
        x.left = deleteMin(x.left); // идем влево до финала
        x.count = 1 + size(x.left) + size(x.right); // обновляем счетчик узлов
        return x; //возвращаемся до root
    }

    //удалить наибольшее значение
    public void deleteMax() {
        if (isEmpty()) throw new NoSuchElementException("Symbol Table Underflow");
        root = deleteMax(root);
    }
    private Node deleteMax(Node x) {
        if (x.right == null) return x.left; //заменяем на левый узел, если он NULL, то весь узел будет NULL
        x.right = deleteMax(x.right); //идем вправо
        x.count = 1 + size(x.left) + size(x.right); // обновляем счетчики узлов
        return x; // возвращаемся до корня
    }

    /**
     * Iterator*/

    public Iterable<Key> keys() {
        if (isEmpty()) return new Queue<>();
        return keys(min(), max());
    }
    public Iterable<Key> keys(Key lo, Key hi) {
        if (lo == null) throw new IllegalArgumentException("first argument to keys() is null");
        if (hi == null) throw new IllegalArgumentException("second argument to keys() is null");
        Queue<Key> q = new Queue<>();
        inorder(root, q, lo, hi);
        return q;
    }
    private void inorder(Node x, Queue<Key> q, Key lo, Key hi) {
        if (x == null) return; // узел равен null возвращаемся
        //сравним lo & hi с root
        int cmplo = lo.compareTo(x.key);
        int cmphi = hi.compareTo(x.key);
        //обходим левое дерево, посещаем корень (Узел), обходим правое дерево
        // и если ключи попадают в диапозон, то записывваем их в Очередь
        if (cmplo < 0) inorder(x.left, q , lo, hi); //если есть куда идти влево, то идем
        if (cmplo <=0 && cmphi >=0) q.enqueue(x.key); //проверяем узел, если попал в диапозон, то записывваем
        if (cmphi > 0) inorder(x.right, q, lo, hi); //пока есть куда идти вправо, идем

    }


    public static void main(String[] args) {
        BST<String, Integer> bst = new BST<>();
        for (int i = 0; !StdIn.isEmpty(); i++) {
            String key = StdIn.readString();
            bst.put(key, i);
        }

        StdOut.println(bst.max());
        StdOut.println();
        for (String s : bst.keys())
           StdOut.println(s + " " + bst.get(s));
    }

}
