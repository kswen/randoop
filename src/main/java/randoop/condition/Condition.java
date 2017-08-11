package randoop.condition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import randoop.util.Log;

/**
 * Defines objects that evaluate a Boolean expression by invoking a {@code
 * java.lang.reflect.Method}.
 *
 * <p>Used to represent a pre- or post-condition of an operation where the expression is given as an
 * expression in Java source code and converted using {@link ConditionMethodCreator}.
 *
 * @see SpecificationTranslator
 */
public class Condition {

  /** The {@code java.lang.reflect.Method} to test this condition */
  private final Method conditionMethod;

  /** The comment describing this condition */
  private final String comment;

  /** The Java source code for this condition. For the format, see {@link #getConditionSource}. */
  private final String conditionSource;

  /**
   * Creates a {@link Condition} that calls the given condition method.
   *
   * @param conditionMethod the reflection {@code Method} for the condition method. For the format,
   *     see {@link #getConditionSource}.
   * @param comment the comment describing this condition
   * @param conditionSource the source code for this condition
   */
  Condition(Method conditionMethod, String comment, String conditionSource) {
    this.conditionMethod = conditionMethod;
    this.comment = comment;
    this.conditionSource = conditionSource;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Condition)) {
      return false;
    }
    Condition other = (Condition) object;
    return this.conditionMethod.equals(other.conditionMethod)
        && this.comment.equals(other.comment)
        && this.conditionSource.equals(other.conditionSource);
  }

  @Override
  public int hashCode() {
    return Objects.hash(conditionMethod, comment, conditionSource);
  }

  @Override
  public String toString() {
    return conditionSource + " // " + comment;
  }

  /**
   * Indicate whether this condition is satisfied by the given values.
   *
   * @param values the values to check the condition against
   * @return true if this condition is satisfied by the values, false otherwise
   */
  public boolean check(Object[] values) {
    try {
      return (boolean) conditionMethod.invoke(null, values);
    } catch (IllegalAccessException e) {
      throw new RandoopConditionError("Failure executing condition method", e);
    } catch (InvocationTargetException e) {
      String message =
          "Failure executing condition method: "
              + conditionMethod
              + " (invoke threw "
              + e.getCause()
              + ")";
      if (Log.isLoggingOn()) {
        Log.logLine(message);
      }
    }
    return false;
  }

  /**
   * Return the code comment for this condition.
   *
   * @return the code comment for this condition
   */
  public String getComment() {
    return comment;
  }

  /**
   * Return the Java source code for this condition. Arguments to the condition follow the {@link
   * randoop.contract.ObjectContract} convention where parameters (including the receiver, if any)
   * are represented by {@code x0}, ..., {@code xn} for some number {@code n}. If the operation has
   * a return value it will be {@code xn} (the last, extra variable).
   *
   * @return the Java representation of the condition as a {@code String}
   */
  public String getConditionSource() {
    return conditionSource;
  }
}
