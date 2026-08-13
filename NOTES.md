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

---

## Topic: Full Page Object Model — LoginPage with Selenium

### Theory
A complete Page Object class needs three things:
1. **Implements an interface** (contract) — defines what actions the page supports
2. **Holds its own WebDriver reference** — received via constructor, stored in a field, used by every method
3. **Real locators inside each method** — found by inspecting the page (right-click → Inspect → read id/name/class)

### Code — BasePage.java (the contract)
```java
package org.example;

interface BasePage {
    void open();
    String getTitle();
    void enterUsername(String username);
    void enterPassword(String password);
    void clickLogin();
}
```

### Code — LoginPage.java (the implementation)
```java
package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

class LoginPage implements BasePage {

    WebDriver driver;

    LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        driver.get("https://www.saucedemo.com");
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public void enterUsername(String username) {
        driver.findElement(By.id("user-name")).sendKeys(username);
    }

    public void enterPassword(String password) {
        driver.findElement(By.id("password")).sendKeys(password);
    }

    public void clickLogin() {
        driver.findElement(By.id("login-button")).click();
    }
}
```

### Code — PageDemo.java (the runner)
```java
package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class PageDemo {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();

        LoginPage lp = new LoginPage(driver);
        lp.open();
        System.out.println(lp.getTitle());
        lp.enterUsername("standard_user");
        lp.enterPassword("secret_sauce");
        lp.clickLogin();
        System.out.println(lp.getTitle());

        driver.quit();
    }
}
```

Swag Labs
https://www.saucedemo.com/inventory.html



### The constructor pattern — `this.driver = driver;`
There are two things named `driver`: the constructor's **parameter** (incoming value) and the class's own **field** (stored on the object). `this.driver` refers to the field; plain `driver` refers to the parameter. This line stores the incoming driver into the object so every other method in the class can use it later.

### Bugs I hit and fixed
| Bug | Cause | Fix / Lesson |
|---|---|---|
| `driver.findElement(By.id(username))` | Confused the locator (where to find the field) with the data (what to type) — passed the actual username string as if it were an element's ID | Locator stays fixed (`By.id("user-name")`); the parameter is the data passed to `.sendKeys()`, not the locator itself |
| Missing `.sendKeys(...)` after `findElement` | `findElement` alone only *finds* an element — does nothing with it | Must chain an action (`.sendKeys()`, `.click()`, etc.) after finding |
| `lp.getTitle();` called but nothing printed | Return value was computed but discarded, not passed to `System.out.println` | Wrap method calls that return a value in `println` if you want to see the result |
| Kept re-running the old file despite editing the new one | IntelliJ's run configuration dropdown was stuck pointing at a previous file (`FirstSeleniumTest`), regardless of which file was open or edited | Click the green triangle directly in the code editor's gutter (next to the class/main line) to force IntelliJ to run that specific file and reset the stuck configuration |

### Why this matters
This is the real, industry-standard Page Object Model structure — the exact pattern used in professional Selenium frameworks with hundreds of pages and tests. The runner class never touches locators or Selenium specifics directly; it only calls clean, readable method names. If saucedemo.com's HTML ever changes, only `LoginPage.java` needs updating — every test using it keeps working unchanged.




**Output:**
### The constructor pattern — `this.driver = driver;`
There are two things named `driver`: the constructor's **parameter** (incoming value) and the class's own **field** (stored on the object). `this.driver` refers to the field; plain `driver` refers to the parameter. This line stores the incoming driver into the object so every other method in the class can use it later.

### Bugs I hit and fixed
| Bug | Cause | Fix / Lesson |
|---|---|---|
| `driver.findElement(By.id(username))` | Confused the locator (where to find the field) with the data (what to type) — passed the actual username string as if it were an element's ID | Locator stays fixed (`By.id("user-name")`); the parameter is the data passed to `.sendKeys()`, not the locator itself |
| Missing `.sendKeys(...)` after `findElement` | `findElement` alone only *finds* an element — does nothing with it | Must chain an action (`.sendKeys()`, `.click()`, etc.) after finding |
| `lp.getTitle();` called but nothing printed | Return value was computed but discarded, not passed to `System.out.println` | Wrap method calls that return a value in `println` if you want to see the result |
| Kept re-running the old file despite editing the new one | IntelliJ's run configuration dropdown was stuck pointing at a previous file (`FirstSeleniumTest`), regardless of which file was open or edited | Click the green triangle directly in the code editor's gutter (next to the class/main line) to force IntelliJ to run that specific file and reset the stuck configuration |

### Why this matters
This is the real, industry-standard Page Object Model structure — the exact pattern used in professional Selenium frameworks with hundreds of pages and tests. The runner class never touches locators or Selenium specifics directly; it only calls clean, readable method names. If saucedemo.com's HTML ever changes, only `LoginPage.java` needs updating — every test using it keeps working unchanged.

# Automation Testing Journey — Notes (Today's Session)

Topics covered today: Selenium WebDriver Setup → Polymorphism → First Real Selenium Test → LoginPage (Full Page Object Model) → InventoryPage (CSS Selectors & Dynamic Locators).

---

## Topic: Selenium WebDriver Setup + Polymorphism

### Adding Selenium via Maven
```xml
<dependencies>
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.27.0</version>
    </dependency>
</dependencies>
```
Must be a sibling of `<properties>`, not nested inside it — each pom.xml section has a specific meaning to Maven's parser.

### What a WebDriver actually does
**Theory:** Selenium needs a "driver" — a translator between Java code and the browser. Since Selenium 4.6+, **Selenium Manager** handles this automatically — detects installed browser version, downloads the matching driver behind the scenes.

**What happens when `new ChromeDriver()` runs:**
1. Selenium Manager detects installed Chrome version
2. Finds/downloads the matching driver executable
3. Starts that driver as a background process
4. That driver process opens a real Chrome window
5. Sets up a live communication channel (WebDriver protocol, over HTTP) between Java code and the browser

```java
public class FirstSeleniumTest {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.saucedemo.com");
        System.out.println("Page title is: " + driver.getTitle());
        driver.quit();
    }
}
```

**Method reference:**
- `driver.get(url)` — navigates to a URL
- `driver.getTitle()` — returns the page's title
- `driver.getCurrentUrl()` — returns the current URL (more reliable success check than title, since some sites keep the same title on every page)
- `driver.quit()` — closes all windows AND properly ends the WebDriver session (always use this, not `close()`)

### Polymorphism
**Definition:** "Poly" = many, "morph" = forms. The same line of code can produce different behavior depending on the actual object behind it, even though the code itself never changes.

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

**In Selenium — WebDriver/ChromeDriver:**
```java
WebDriver driver = new ChromeDriver();
driver.get("https://...");   // runs ChromeDriver's implementation of get()

WebDriver driver = new FirefoxDriver();
driver.get("https://...");   // same line, controls Firefox instead
```

**Key vocabulary:** `driver` is a variable reference — its *declared type* is the interface (`WebDriver`), but it *points to* an object of a concrete implementing class (`ChromeDriver`). This is polymorphism.

**Method parameters accept any implementing class:**
```java
void runTest(WebDriver driver) {
    driver.get("https://www.saucedemo.com");
}
runTest(new ChromeDriver());     // valid
runTest(new FirefoxDriver());    // also valid, no code change needed
```

**How to recognize if something in code is an interface:**
1. Ctrl+Click on the type name in IntelliJ — jumps to its definition
2. IntelliJ shows a different icon for interfaces vs. classes
3. Naming conventions are NOT reliable — `List`, `Map`, `Set`, `WebDriver` are all interfaces with no special prefix

### Error and Solution
**Error:** "Non-parseable POM" XML error.
**Cause:** `<dependencies>` was nested inside `<properties>` instead of as a sibling.
**Solution:** Moved `<dependencies>` to be a direct child of `<project>`, sitting right after `</properties>` closes.

**Error:** Duplicate `<dependencies>` tag causing parse errors.
**Cause:** Copy-paste error created two opening tags.
**Solution:** XML requires exactly one matching closing tag per opening tag — removed the duplicate.

### Interview Questions
**Q1: What is polymorphism, and what are its two main types in Java?**
A: The ability for the same interface/method call to behave differently depending on the actual object involved. Compile-time (static) — method overloading, resolved at compile time. Runtime (dynamic) — method overriding, resolved at runtime based on the actual object type.

**Q2: How does `WebDriver driver = new ChromeDriver();` demonstrate polymorphism?**
A: `driver` is declared as the interface type `WebDriver`, but references an actual `ChromeDriver` object. Calling `driver.get(...)` at runtime executes `ChromeDriver`'s specific implementation.

**Q3: Why is this pattern essential for cross-browser testing frameworks?**
A: All test code is written against the `WebDriver` interface, so only the object-creation line needs to change to target a different browser — every other line remains identical and reusable.

**Q4: What's the difference between method overloading and method overriding?**
A: Overloading — multiple methods, same name, different parameters, same class, compile-time resolved. Overriding — a subclass/implementer provides its own version of a parent/interface method, runtime resolved.

**Q5: Can you have a variable of an interface type never assigned a concrete object?**
A: Yes, it holds `null` until assigned — calling a method on it before assignment throws `NullPointerException`.

**Q6: If WebDriver had ten implementing classes, would your test code need to know about all ten?**
A: No — test code only needs to know the WebDriver interface's contract, never implementation details.

---

## Topic: LoginPage — Full Page Object Model

### Code
```java
// BasePage.java
interface BasePage {
    void open();
    String getTitle();
    void enterUsername(String username);
    void enterPassword(String password);
    void clickLogin();
}

// LoginPage.java
class LoginPage implements BasePage {
    WebDriver driver;
    LoginPage(WebDriver driver) { this.driver = driver; }
    public void open() { driver.get("https://www.saucedemo.com"); }
    public String getTitle() { return driver.getTitle(); }
    public void enterUsername(String username) {
        driver.findElement(By.id("user-name")).sendKeys(username);
    }
    public void enterPassword(String password) {
        driver.findElement(By.id("password")).sendKeys(password);
    }
    public void clickLogin() {
        driver.findElement(By.id("login-button")).click();
    }
}

// PageDemo.java (runner)
public class PageDemo {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        LoginPage lp = new LoginPage(driver);
        lp.open();
        System.out.println(lp.getTitle());
        lp.enterUsername("standard_user");
        lp.enterPassword("secret_sauce");
        lp.clickLogin();
        System.out.println(lp.getTitle());
        driver.quit();
    }
}
```

**Output:**
```
Swag Labs
https://www.saucedemo.com/inventory.html
```

### The constructor pattern — `this.driver = driver;`
Two things named `driver`: the constructor's **parameter** (incoming value) and the class's own **field** (stored on the object). `this.driver` refers to the field; plain `driver` refers to the parameter. This line stores the incoming driver so every other method in the class can use it later.

### close() vs quit()
| Method | What it does |
|---|---|
| `driver.close()` | Closes only the current tab/window; driver process may keep running in background |
| `driver.quit()` | Closes all windows AND properly ends the entire WebDriver session |

### Error and Solution
**Error:** `driver.findElement(By.id(username))` — tried to use the username value itself as a locator.
**Cause:** Confused the locator (fixed, "where to find") with the data (variable, "what to type").
**Solution:** `driver.findElement(By.id("user-name")).sendKeys(username)` — fixed locator, variable data passed to sendKeys.

**Error:** "Connection reset" `SocketException` warning after test.
**Cause:** Used `driver.close()` instead of `driver.quit()` — background driver process wasn't shut down cleanly.
**Solution:** Switched to `driver.quit()`, warning disappeared.

**Error:** Verified login using `getTitle()` — printed identical value before and after.
**Cause:** saucedemo.com uses the same page title on every page.
**Solution:** Used `driver.getCurrentUrl()` instead — a value that actually changes on success.

**Error:** Kept re-running an old file (`FirstSeleniumTest`) despite editing `PageDemo`.
**Cause:** IntelliJ's run configuration dropdown was stuck pointing at a previous file.
**Solution:** Clicked the green triangle directly in the code editor's gutter next to the class/main line — forces IntelliJ to run that specific file and resets the stuck configuration.

### Interview Questions
**Q1: What are the different Selenium locator strategies, and how do you pick one?**
A: id, name, className, tagName, linkText, partialLinkText, cssSelector, xpath. Priority: id first → name → unique cssSelector → xpath as last resort.

**Q2: XPath vs CSS Selector — which is faster, which is more powerful?**
A: CSS is generally faster (native browser optimization). XPath is more powerful (can traverse up the DOM, match by visible text) but slower and more fragile to DOM changes.

**Q3: What problem does Page Object Model actually solve?**
A: Code duplication (same locators reused across tests) and maintainability (UI changes only require updating one page class, not every test that touches that element).

**Q4: findElement() vs findElements()?**
A: findElement — returns a single WebElement, throws NoSuchElementException if not found. findElements — returns a List<WebElement>, returns an empty list if none found.

**Q5: How do you handle a dynamically-changing element ID?**
A: Avoid the dynamic part — use cssSelector/xpath targeting a stable attribute (data-test, name), a partial match (`contains()` in XPath, `*=` in CSS), or locate relative to a stable parent element.

**Q6: In a framework with 50+ page classes, how do you avoid repeating the same driver constructor everywhere?**
A: Create a common (often abstract) BasePage class holding the WebDriver field and constructor once; every page class extends it and inherits that logic.

---

## Topic: InventoryPage — CSS Selectors and Dynamic Locators

### Code
```java
public class InventoryPage {
    WebDriver driver;
    InventoryPage(WebDriver driver) { this.driver = driver; }

    String getPageHeaderText() {
        return driver.findElement(By.cssSelector("[data-test='title']")).getText();
    }
    void addItemToCart(String productId) {
        driver.findElement(By.id(productId)).click();
    }
    String getCartBadgeCount() {
        return driver.findElement(By.className("shopping_cart_badge")).getText();
    }
}
```

**Output (from runner calling login + inventory flow):**
```
Swag Labs
https://www.saucedemo.com/inventory.html
Products
Cart count: 1
```

### Locator strategy reference
| Strategy | Example | When to use |
|---|---|---|
| `By.id(...)` | `By.id("login-button")` | Best — fastest, most reliable, use whenever available |
| `By.name(...)` | `By.name("username")` | Good fallback if no id, but a name attribute exists |
| `By.className(...)` | `By.className("btn-primary")` | Risky if the class is shared/generic |
| `By.cssSelector(...)` | `By.cssSelector("[data-test='title']")` | Flexible — target any attribute, combine classes, nested elements |
| `By.xpath(...)` | `By.xpath("//span[text()='Products']")` | Most powerful, can locate by visible text — slower, more fragile |

### data-test attributes
Custom HTML attributes added by developers specifically as stable automation hooks, independent of CSS classes used for styling. Prefer them over generic classes when present.

### Error and Solution
**Error:** `NoSuchElementException` on `By.className("add-to-cart-sauce-labs-backpack")`.
**Cause:** That string was the element's `id`, not its class.
**Solution:** Changed to `By.id(productId)`, using the method's own parameter instead of a hardcoded string — fixes the bug and makes the method reusable for any product.

**Error:** Compile error chaining `.getText()` onto `By.cssSelector(...)` directly.
**Cause:** `By` objects don't have `getText()` — only `WebElement` (returned by findElement) does.
**Solution:** `driver.findElement(By.cssSelector("...")).getText()` — getText() chained after findElement resolves the actual element.

**Error:** Cart count computed but never printed.
**Cause:** Return value discarded, not passed to println.
**Solution:** Wrapped the call in `System.out.println(...)`.

### Interview Questions
**Q1: What are data-* attributes, and why do QA-friendly sites include them?**
A: Custom attributes added specifically as stable automation hooks, independent of styling-related CSS classes. Best practice to prefer them when present.

**Q2: Why accept a parameter (productId) instead of hardcoding a locator inside a method?**
A: Hardcoding limits the method to one specific element. Building the locator from a parameter makes the same method reusable across many elements.

**Q3: What does driver.findElement(...).getText() do step by step?**
A: findElement locates and returns a WebElement; getText() reads that element's visible, rendered text as a String.

**Q4: Why can't you call getText() on a By object directly?**
A: By only describes how to locate an element — it isn't a found element. Only WebElement objects have interaction methods like getText(), click(), sendKeys().

**Q5: Risk of By.xpath with visible text, e.g. //span[text()='Products']?**
A: Fragile — breaks if displayed text changes (copy edits, translations), even if the element's structure is unchanged.

**Q6: In POM, why doesn't a page class need a main method, and where does test logic live?**
A: Page classes only expose available actions/data. Actual test sequencing and assertions live in a separate runner/test class.

**Q7: How do you verify an action like "Add to Cart" actually worked, beyond the click succeeding?**
A: Check for a resulting state change — e.g., read the cart badge count after the click and confirm it increased, rather than assuming success from the click alone.

---

## Environment reminder (from today)
- Portfolio repo: github.com/prasanthkankati/automation-testing-journey
- Standard push after each topic:
```
git add .
git commit -m "Descriptive message"
git push
```

---

## Topic: TestNG — Converting to a Real Test Framework

### Theory
TestNG replaces a bare `main()` runner with proper testing structure:
- **`@Test`** — marks a method as an actual test case; TestNG discovers and runs it automatically, with zero parameters allowed (unless using `@DataProvider`, a later topic)
- **`@BeforeMethod`** / **`@AfterMethod`** — setup/teardown that runs automatically before/after *every* `@Test` method in the class
- **`Assert.assertTrue(...)` / `assertEquals(...)`** — replaces manual `System.out.println` + eyeballing; TestNG automatically fails the test with a clear report if the assertion doesn't hold

**Key structural rule:** one `@Test` method represents one complete test scenario (open → act → verify), not one individual action. Reuses existing Page Object classes entirely — the test class never contains locators or raw Selenium calls.

### pom.xml — adding TestNG
```xml
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.10.2</version>
    <scope>test</scope>
</dependency>
```
`<scope>test</scope>` means: only visible to code inside `src/test/java`, not `src/main/java`.

### Maven's standard folder convention
- **`src/main/java`** — reusable/production code (Page Object classes: LoginPage, InventoryPage, BasePage)
- **`src/test/java`** — actual test classes (anything with `@Test`)

This isn't arbitrary — Maven enforces it via dependency scoping, and every real framework follows this split.

### Code — LoginTest.java (in src/test/java/org/example)
```java
package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.Assert;

public class LoginTest {

    WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
    }

    @Test
    public void verifyLoginSuccess(){
        LoginPage lp = new LoginPage(driver);
        lp.open();
        lp.enterUsername("standard_user");
        lp.enterPassword("secret_sauce");
        lp.clickLogin();
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @AfterMethod
    public void close() {
        driver.quit();
    }
}
```

**Output (via Maven test lifecycle):**