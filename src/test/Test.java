
public class Test {

	public static void main(String[] args) {
		Test hello = new Test();
		B b = hello.new B();
		System.out.println(b instanceof A);
	}

	public class A {

	}

	public class B extends A {

	}

}
