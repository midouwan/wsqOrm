package com.wsq.orm.utils;

import org.springframework.lang.Nullable;

import java.util.function.Supplier;

public abstract class Assert {

	/**
	 * Assert a boolean expression, throwing an {@code IllegalStateException}
	 * if the expression evaluates to {@code false}.
	 * <p>Call {@link #isTrue} if you wish to throw an {@code IllegalArgumentException}
	 * on an assertion failure.
	 * <pre class="code">Assert.state(id == null, "The id property must not already be initialized");</pre>
	 * @param expression a boolean expression
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalStateException if {@code expression} is {@code false}
	 */
	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}

	/**
	 * Assert a boolean expression, throwing an {@code IllegalStateException}
	 * if the expression evaluates to {@code false}.
	 * <p>Call {@link #isTrue} if you wish to throw an {@code IllegalArgumentException}
	 * on an assertion failure.
	 * <pre class="code">
	 * Assert.state(id == null,
	 *     () -&gt; "ID for " + entity.getName() + " must not already be initialized");
	 * </pre>
	 * @param expression a boolean expression
	 * @param messageSupplier a supplier for the exception message to use if the
	 * assertion fails
	 * @throws IllegalStateException if {@code expression} is {@code false}
	 * @since 5.0
	 */
	public static void state(boolean expression, Supplier<String> messageSupplier) {
		if (!expression) {
			throw new IllegalStateException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 * @deprecated as of 4.3.7, in favor of {@link #state(boolean, String)}
	 */
	@Deprecated
	public static void state(boolean expression) {
		state(expression, "[Assertion failed] - this state invariant must be true");
	}

	/**
	 * Assert a boolean expression, throwing an {@code IllegalArgumentException}
	 * if the expression evaluates to {@code false}.
	 * <pre class="code">Assert.isTrue(i &gt; 0, "The value must be greater than zero");</pre>
	 * @param expression a boolean expression
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if {@code expression} is {@code false}
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert a boolean expression, throwing an {@code IllegalArgumentException}
	 * if the expression evaluates to {@code false}.
	 * <pre class="code">
	 * Assert.isTrue(i &gt; 0, () -&gt; "The value '" + i + "' must be greater than zero");
	 * </pre>
	 * @param expression a boolean expression
	 * @param messageSupplier a supplier for the exception message to use if the
	 * assertion fails
	 * @throws IllegalArgumentException if {@code expression} is {@code false}
	 * @since 5.0
	 */
	public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
		if (!expression) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 * @deprecated as of 4.3.7, in favor of {@link #isTrue(boolean, String)}
	 */
	@Deprecated
	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - this expression must be true");
	}

	/**
	 * Assert that an object is {@code null}.
	 * <pre class="code">Assert.isNull(value, "The value must be null");</pre>
	 * @param object the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is not {@code null}
	 */
	public static void isNull(@Nullable Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that an object is {@code null}.
	 * <pre class="code">
	 * Assert.isNull(value, () -&gt; "The value '" + value + "' must be null");
	 * </pre>
	 * @param object the object to check
	 * @param messageSupplier a supplier for the exception message to use if the
	 * assertion fails
	 * @throws IllegalArgumentException if the object is not {@code null}
	 * @since 5.0
	 */
	public static void isNull(@Nullable Object object, Supplier<String> messageSupplier) {
		if (object != null) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 * Assert that an object is not {@code null}.
	 * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
	 * @param object the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is {@code null}
	 */
	public static void notNull(@Nullable Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Assert that an object is not {@code null}.
	 * <pre class="code">
	 * Assert.notNull(clazz, () -&gt; "The class '" + clazz.getName() + "' must not be null");
	 * </pre>
	 * @param object the object to check
	 * @param messageSupplier a supplier for the exception message to use if the
	 * assertion fails
	 * @throws IllegalArgumentException if the object is {@code null}
	 * @since 5.0
	 */
	public static void notNull(@Nullable Object object, Supplier<String> messageSupplier) {
		if (object == null) {
			throw new IllegalArgumentException(nullSafeGet(messageSupplier));
		}
	}

	/**
	 * Assert that an array contains no {@code null} elements.
	 * <p>Note: Does not complain if the array is empty!
	 * <pre class="code">Assert.noNullElements(array, "The array must contain non-null elements");</pre>
	 * @param array the array to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object array contains a {@code null} element
	 */
	public static void noNullElements(@Nullable Object[] array, String message) {
		if (array != null) {
			for (Object element : array) {
				if (element == null) {
					throw new IllegalArgumentException(message);
				}
			}
		}
	}

	/**
	 * Assert that an array contains no {@code null} elements.
	 * <p>Note: Does not complain if the array is empty!
	 * <pre class="code">
	 * Assert.noNullElements(array, () -&gt; "The " + arrayType + " array must contain non-null elements");
	 * </pre>
	 * @param array the array to check
	 * @param messageSupplier a supplier for the exception message to use if the
	 * assertion fails
	 * @throws IllegalArgumentException if the object array contains a {@code null} element
	 * @since 5.0
	 */
	public static void noNullElements(@Nullable Object[] array, Supplier<String> messageSupplier) {
		if (array != null) {
			for (Object element : array) {
				if (element == null) {
					throw new IllegalArgumentException(nullSafeGet(messageSupplier));
				}
			}
		}
	}
	private static boolean endsWithSeparator(String msg) {
		return (msg.endsWith(":") || msg.endsWith(";") || msg.endsWith(",") || msg.endsWith("."));
	}

	private static String messageWithTypeName(String msg, @Nullable Object typeName) {
		return msg + (msg.endsWith(" ") ? "" : ": ") + typeName;
	}

	@Nullable
	private static String nullSafeGet(@Nullable Supplier<String> messageSupplier) {
		return (messageSupplier != null ? messageSupplier.get() : null);
	}

}
