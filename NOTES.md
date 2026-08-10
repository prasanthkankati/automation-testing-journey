# Automation Testing Journey — Notes

---

## Topic: Java Collections (List, Map, Set)

### 1. List — ordered, allows duplicates

**Theory:** Stores items in the order you add them. Same value can appear more than once. Use when sequence matters — e.g., a list of test cases to run in a specific order.

**Code:**
```java
List<String> testCases = new ArrayList<>();
testCases.add("Login Test");
testCases.add("Checkout Test");
testCases.add("Search Test");
```

---

### 2. Map — key-value pairs, keys unique

**Theory:** Stores data as key → value. Keys cannot repeat (adding a duplicate key overwrites the old value). Use when you need to look something up by a name — e.g., test case name → its result.

**Code:**
```java
Map<String, String> results = new HashMap<>();
results.put("Login Test", "Pass");
results.put("Checkout Test", "Fail");
results.put("Search Test", "Pass");
```

**Looping through a Map — the part that was confusing:**

Theory: A Map holds pairs, so you can't loop through it like a List (single values). `entrySet()` converts the Map into a collection of `Map.Entry` objects — think of each `Entry` as one "envelope" holding one key and one value together. The loop hands you one envelope per cycle; `.getKey()` and `.getValue()` open it.

**Code:**
```java
int passCount = 0, failCount = 0;

for (Map.Entry<String, String> entry : results.entrySet()) {
    System.out.println(entry.getKey() + " : " + entry.getValue());

    if (entry.getValue().equals("Pass")) {
        passCount++;
        System.out.println("Counted as Pass :" + passCount);
    } else {
        failCount++;
        System.out.println("Counted as Fail :" + failCount);
    }
}

System.out.println("Total Pass: " + passCount + ", Total Fail: " + failCount);
```

**Bug I hit:** Wrote `else failCount++;` without curly braces, then put the print statement on the next line thinking it was still part of `else`. In Java, without `{ }`, only the *single next statement* belongs to if/else — so the print line ran unconditionally every time, even for Pass entries. **Fix: always use `{ }` on if/else, even for one line.**

---

### 3. Set — only unique values, no duplicates

**Theory:** Automatically rejects duplicate values — adding the same value twice has no effect the second time. Three variants control the print/iteration order differently, but all three reject duplicates the same way.

**Code — comparing all three:**
```java
// HashSet — unpredictable order (hash-bucket based)
Set<String> browserHash = new HashSet<>();
browserHash.add("Firefox");
browserHash.add("Safari");
browserHash.add("Chrome");
System.out.println(browserHash);   // order not guaranteed

// LinkedHashSet — preserves insertion order
Set<String> browserLinked = new LinkedHashSet<>();
browserLinked.add("Firefox");
browserLinked.add("Safari");
browserLinked.add("Chrome");
System.out.println(browserLinked); // [Firefox, Safari, Chrome]

// TreeSet — always sorted
Set<String> browserTree = new TreeSet<>();
browserTree.add("Firefox");
browserTree.add("Safari");
browserTree.add("Chrome");
System.out.println(browserTree);   // [Chrome, Firefox, Safari]
```

| Type | Duplicates | Order |
|---|---|---|
| `HashSet` | Rejected | Unpredictable |
| `LinkedHashSet` | Rejected | Insertion order |
| `TreeSet` | Rejected | Always sorted |

---

### Bugs & lessons log
| Bug | Cause | Fix / Lesson |
|---|---|---|
| Print ran on every loop, not just Fail | Missing `{ }` on else | Always brace if/else, even one-liners |
| Output didn't match code | Ran a stale/wrong file version | Always confirm actual file content before debugging "wrong" output |
| "Serach Test" typo | Manual typing mismatch | Shows how easily test data typos cause silent bugs |

### Key takeaway
Predict expected output *before* running, then compare to actual — this is a core debugging habit, not just a learning exercise.


---

## Topic: Interfaces & Page Object Pattern (mini version)

### What is an interface?
**Theory:** An interface is a contract — it declares *what* methods a class must have, but never *how* they work (no method bodies). A class that implements an interface must provide real code for every method the interface declares. Interfaces can't have constructors and can't be instantiated directly.

**Code — the interface (contract):**
```java
package org.example;

public interface BasePage {
    void open();
    String getTitle();
}
```

### Implementing the interface
**Theory:** A class "signs" the contract using `implements`. It must then provide real bodies for every method the interface declared. Important rule: methods in an interface are automatically `public`, so implementing classes must also mark them `public` — a compile error occurs otherwise ("attempting to assign weaker access privileges").

**Code — the class (real behavior):**
```java
package org.example;

public class LoginPage implements BasePage {

    public void open() {
        System.out.println("Opening the loginPage");
    }

    public String getTitle() {
        return "LoginPage";
    }
}
```

### Using it — separate runner class
**Theory:** Real frameworks keep "page" classes (what a page can do) separate from "runner/test" classes (what actually calls those actions). Page classes never contain `main`.

**Code:**
```java
package org.example;

public class PageDemo {
    public static void main(String[] args) {
        LoginPage lp = new LoginPage();
        lp.open();
        System.out.println(lp.getTitle());
    }
}
```

**Output:**

Opening the 

LoginPage