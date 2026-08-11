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



---

## Topic: Exception Handling (try / catch / finally)

### Theory
An exception is a runtime error — code that's valid and compiles fine, but something goes wrong while it's actually running (e.g., accessing an array index that doesn't exist). Without handling it, the program crashes immediately with a stack trace and stops execution (exit code 1).

- **try** — wraps code that might fail
- **catch** — runs only if an exception occurs; lets you handle it gracefully instead of crashing
- **finally** — always runs, whether an exception occurred or not; used for cleanup (e.g., closing a browser in Selenium regardless of test outcome)

### Code
```java
package org.example;

public class ExceptionHandling {

    public static void main(String[] args) {
        int[] arr = {10, 20, 30};

        try {
            System.out.println(arr[5]);   // throws ArrayIndexOutOfBoundsException
        } catch (Exception e) {
            System.out.println("Error: Tried to access an index that doesn't exist - " + e.getMessage());
        } finally {
            System.out.println("Done attempting to access array");
        }
    }
}
```

**Output:**


Error: Tried to access an index that doesn't exist - Index 5 out of bounds for length 3
Done attempting to access array


### Comparison — with vs. without try/catch
| Without try/catch | With try/catch |
|---|---|
| Program crashes immediately | Program continues running |
| Ugly stack trace printed | Clean, custom error message |
| Exit code 1 (failure) | Exit code 0 (success) |

### Bugs I hit and fixed
| Bug | Cause | Fix / Lesson |
|---|---|---|
| `catch` block re-threw the exception (`throw new RuntimeException(e)`) | Misunderstood the purpose of catch | Catch should *handle* the problem, not just relabel and crash anyway |
| Missing `public` on `main` | Same mistake as Interfaces topic | Commit `public static void main(String[] args)` to memory |
| Output didn't match latest code (stale build, again) | File wasn't saved before running | Turn on auto-save in IntelliJ settings to eliminate this permanently |

### Why this matters for automation
Selenium throws exceptions constantly — element not found, timeout waiting for a page to load, stale element references. A test suite without proper exception handling crashes entirely on the first failure. A well-built framework catches specific exceptions, logs a clear failure reason, and moves on to the next test — this is the difference between a suite that gives you a useful failure report and one that just dies silently on test #1 of 200.

---

## Topic: File I/O (Reading Files)

### Theory
Real automation frameworks read test data from external files (Excel, CSV, JSON, plain text) instead of hardcoding it — this is the foundation of **data-driven testing**, where the same test logic runs against many different data sets without touching the code.

**The reading chain (analogy: reading a book):**
- **File path (String)** — the book sitting on a shelf, not open yet
- **FileReader** — opens the file, reads raw characters
- **BufferedReader** — wraps around FileReader, adds the ability to read whole lines at once via `readLine()`

You always build them together — FileReader does the actual opening; BufferedReader wraps it for convenient line-by-line reading.

**Checked exceptions:** `IOException` is a *checked* exception — Java's compiler forces you to handle it (via try/catch) or the code won't even compile. This is different from runtime exceptions like array-index errors, which compile fine but can crash later. File operations are risky (file missing, no permission) so Java makes you acknowledge that risk upfront.

### Code
```java
package org.example;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class FileReadingPractice {

    public static void main(String[] args) {
        BufferedReader br = null;   // declared outside try, so finally can reach it

        try {
            FileReader fr = new FileReader("src/main/resources/testdata.txt");
            br = new BufferedReader(fr);

            String line;
            while ((line =



Login Test
Checkout Test
Search Test


**Output:**

Login Test
Checkout Test
Search Test


Login Test
Checkout Test
Search Test


**Output:**

Login Test
Checkout Test
Search Test

---

## Topic: Selenium WebDriver Setup + Polymorphism

### Part 1: Adding Selenium to a Maven project

**Theory:** Selenium isn't part of core Java — it's an external library. Maven manages external libraries ("dependencies") through `pom.xml`. Declaring a dependency tells Maven: download this specific library (and everything it needs) from Maven's central repository, and make it available to your code.

**Code — pom.xml dependency block:**
```xml
<dependencies>
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.27.0</version>
    </dependency>
</dependencies>
```
- `groupId` — the organization that publishes the library
- `artifactId` — the specific library name
- `version` — which release of that library to use

**Important structural rule:** `<dependencies>` must sit as a direct child of `<project>`, as a sibling to `<properties>` — NOT nested inside `<properties>`. Each top-level pom.xml section has a specific meaning to Maven's parser; nesting the wrong section inside another causes a "Non-parseable POM" XML error.

---

### Part 2: What a WebDriver actually does

**Theory:** Selenium needs a "driver" — a translator program between your Java code and the actual browser. Since Selenium 4.6+, this is handled automatically by **Selenium Manager** — it detects your installed browser version and downloads the matching driver behind the scenes. No manual ChromeDriver.exe downloads needed.

**Code — first working Selenium script:**
```java
package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class FirstSeleniumTest {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.saucedemo.com");

        System.out.println("Page title is: " + driver.getTitle());

        driver.quit();
    }
}
```

**What happens when `new ChromeDriver()` runs, step by step:**
1. Selenium Manager detects installed Chrome version
2. Finds/downloads the matching driver executable
3. Starts that driver as a background process
4. That driver process opens a real Chrome window
5. Sets up a live communication channel (WebDriver protocol, over HTTP) between your Java code and the actual browser

This is fundamentally different from a normal object like `new ArrayList<>()` — it's not just data in memory, it's controlling a real external process on your machine.

**Method reference:**
- `driver.get(url)` — navigates to a URL
- `driver.getTitle()` — returns the page's title as a String
- `driver.quit()` — closes the browser and ends the session (always call this — leaving browser sessions open wastes memory and can cause issues across a long test run)

---

### Part 3: Polymorphism

**Definition:** "Poly" = many, "morph" = forms. Polymorphism means **the same line of code can produce different behavior depending on the actual object behind it**, even though the code itself never changes.

**Simple example (Animal):**
```java
public interface Animal {
    void makeSound();
}

public class Dog implements Animal {
    public void makeSound() { System.out.println("Woof!"); }
}

public class Cat implements Animal {
    public void makeSound() { System.out.println("Meow!"); }
}
```
```java
Animal a1 = new Dog();
Animal a2 = new Cat();

a1.makeSound();   // Woof!
a2.makeSound();   // Meow!
```
Same declared type (`Animal`), same method call (`makeSound()`), different actual behavior — because the real object underneath differs.

**In my own code — BasePage/LoginPage:**
```java
BasePage page = new LoginPage();
page.open();   // runs LoginPage's specific open() implementation
```

**In Selenium — WebDriver/ChromeDriver:**
```java
WebDriver driver = new ChromeDriver();
driver.get("https://...");   // runs ChromeDriver's implementation of get()

// If swapped:
WebDriver driver = new FirefoxDriver();
driver.get("https://...");   // same line, but now controls Firefox instead
```

**Key vocabulary:**
- `driver` is a **variable reference** — its *declared type* is the interface (`WebDriver`), but it *points to* an object of a concrete implementing class (`ChromeDriver`)
- This — a parent/interface-typed variable referring to a child/implementing-class object — IS polymorphism in Java terms

**Proof of understanding — method parameters accept any implementing class:**
```java
void runTest(WebDriver driver) {
    driver.get("https://www.saucedemo.com");
}

runTest(new ChromeDriver());     // valid
runTest(new FirefoxDriver());    // also valid, no code change needed
```
Java only checks that the passed object fulfills the declared interface type — it doesn't care which concrete class actually provided that fulfillment.

**Why this matters in real frameworks (the actual interview-worthy point):**
This is exactly how **cross-browser testing** works. A framework has one central setup method that picks which browser to instantiate based on config:
```java
WebDriver driver;
if (browserName.equals("chrome")) {
    driver = new ChromeDriver();
} else if (browserName.equals("firefox")) {
    driver = new FirefoxDriver();
}
```
Every test method in the entire suite is written using only `driver.get(...)`, `driver.findElement(...)`, etc. — never knowing or caring which browser is actually running. Without polymorphism, you'd need to duplicate the entire test suite once per browser.

**How to recognize if something in code is an interface:**
1. Ctrl+Click (Cmd+Click on Mac) on the type name in IntelliJ — jumps to its definition; if it says `public interface X`, it's an interface
2. IntelliJ shows a different icon for interfaces vs. classes in the project tree/autocomplete
3. Naming conventions are NOT reliable — Java's own standard types (`List`, `Map`, `Set`, `WebDriver`) are all interfaces with no special prefix

### Bugs I hit and fixed
| Bug | Cause | Fix / Lesson |
|---|---|---|
| "Non-parseable POM" error | `<dependencies>` nested inside `<properties>` instead of as a sibling | Each pom.xml section has a specific meaning; check structure, not just tag spelling |
| Duplicate `<dependencies>` tag | Copy-paste error created two opening tags | XML requires exactly one matching closing tag per opening tag |

### Interview-ready summary (say this out loud to practice)
"Polymorphism means a variable declared as an interface type can hold any object that implements that interface, and calling a method on it runs whichever implementation that specific object actually provides. In Selenium, `WebDriver driver = new ChromeDriver()` is a real-world example — my test code is written entirely against the `WebDriver` interface, so switching browsers, or even switching automation tools that follow a similar pattern, requires changing only the object creation line, not the rest of my test logic. This is what enables cross-browser testing frameworks to scale without duplicating code."

---

## Topic: First Real Selenium Test — Login Automation

### Theory
Interacting with a page requires two things:
1. **A locator** — tells Selenium where an element is (By.id, By.name, By.className, By.xpath, etc.)
2. **An action** — what to do once found (.click(), .sendKeys(), .getText(), etc.)

`driver.findElement(By.locatorType("value"))` returns a `WebElement` object; you call actions directly on that returned object.

**Finding real locators:** right-click an element in the actual browser → Inspect → read its HTML attributes (id, name, class) from DevTools. This is the manual-tester skill of "reading a page's structure," now applied to automation.

### Code
```java
package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class FirstSeleniumTest {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.saucedemo.com");

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        System.out.println("Current URL after login: " + driver.getCurrentUrl());

        driver.quit();
    }
}
```

**Output:**

### Part D: First Real Selenium Test — Login Automation

**Theory:** Interacting with a page requires a **locator** (`By.id`, `By.name`, `By.xpath`, etc.) to find an element, and an **action** (`.click()`, `.sendKeys()`, `.getText()`) to do something with it. `driver.findElement(By.locatorType("value"))` returns a `WebElement` you call actions on directly. Find real locators by right-clicking an element in the browser → Inspect → reading its HTML attributes.

**Code:**
```java
package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class FirstSeleniumTest {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.saucedemo.com");

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        System.out.println("Current URL after login: " + driver.getCurrentUrl());

        driver.quit();
    }
}
```

**Output:**

Current URL after login: https://www.saucedemo.com/inventory.html


**close() vs quit():**
| Method | What it does |
|---|---|
| `driver.close()` | Closes only the current tab/window; driver process may keep running in background |
| `driver.quit()` | Closes all windows AND properly ends the entire WebDriver session |

---

### Bugs & lessons log (full topic)
| Bug | Cause | Fix / Lesson |
|---|---|---|
| "Non-parseable POM" error | `<dependencies>` nested inside `<properties>` instead of as a sibling | Each pom.xml section has a specific meaning; check structure, not just tag spelling |
| Duplicate `<dependencies>` tag | Copy-paste error created two opening tags | XML requires exactly one matching closing tag per opening tag |
| Verified login using `getTitle()` — identical before/after | saucedemo.com uses the same page title on every page | Never verify success with something that doesn't actually change — use `getCurrentUrl()` instead |
| "Connection reset" warning after test | Used `driver.close()` instead of `driver.quit()` | `quit()` properly ends the whole session; `close()` only shuts the visible window |
| CDP version warning in console | Selenium's bundled DevTools module doesn't exactly match installed Chrome version | Harmless for basic actions — only matters for advanced network-interception features later |

### Interview-ready summary (say this out loud to practice)
"Polymorphism means a variable declared as an interface type can hold any object that implements that interface, and calling a method on it runs whichever implementation that specific object actually provides. In Selenium, `WebDriver driver = new ChromeDriver()` is a real-world example — my test code is written entirely against the `WebDriver` interface, so switching browsers requires changing only the object creation line, not the rest of my test logic. This is what enables cross-browser testing frameworks to scale without duplicating code."

### Why this matters for automation
This is a real, working login automation — the exact shape of production Selenium tests, not yet organized into a proper framework. Next step: wrap this into a proper Page Object class, combining the BasePage/LoginPage interface pattern with real Selenium locators inside.
