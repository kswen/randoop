// ***** This file is automatically generated from PairwiseIntComparison.java.jpp

package daikon.inv.binary.twoSequence;

import daikon.*;
import daikon.inv.*;
import daikon.inv.binary.twoScalar.*;
import daikon.derive.binary.*;
import daikon.suppress.*;
import daikon.Quantify.QuantFlags;

import utilMDE.ArraysMDE;
import utilMDE.Assert;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents an invariant between corresponding elements of two
 * sequences of double values.  The length of the sequences must match for
 * the invariant to hold.  A comparison is made over each
 * <samp>(x[i], y[i])</samp> pair.
 * Thus, <samp>x[0]</samp> is compared to <samp>y[0]</samp>,
 * <samp>x[1]</samp> to <samp>y[1]</samp>, and so forth.
 * Prints as <samp>x[] > y[]</samp>.
 **/

public class PairwiseFloatGreaterThan
  extends TwoSequenceFloat
{
  // We are Serializable, so we specify a version to allow changes to
  // method signatures without breaking serialization.  If you add or
  // remove fields, you should change this number to the current date.
  static final long serialVersionUID = 20030822L;

  /** Debug tracer. **/
  public static final Logger debug =
    Logger.getLogger("daikon.inv.binary.twoSequence.PairwiseFloatGreaterThan");

  // Variables starting with dkconfig_ should only be set via the
  // daikon.config.Configuration interface.
  /**
   * Boolean.  True iff PairwiseIntComparison invariants should be considered.
   **/
  public static boolean dkconfig_enabled = true;

  static final boolean debugPairwiseIntComparison = false;

  protected PairwiseFloatGreaterThan(PptSlice ppt) {
    super(ppt);
  }

  private static PairwiseFloatGreaterThan proto;

  /** Returns the prototype invariant for PairwiseFloatGreaterThan **/
  public static Invariant get_proto() {
    if (proto == null)
      proto = new PairwiseFloatGreaterThan ((PptSlice) null);
    return (proto);
  }

  /** Returns whether or not this invariant is enabled **/
  public boolean enabled() {
    return dkconfig_enabled;
  }

  /** PairwiseFloatGreaterThan is only valid on integral types **/
  public boolean instantiate_ok (VarInfo[] vis) {

    if (!valid_types (vis))
      return (false);

      if (!(vis[0].type.elementIsFloat() && vis[1].type.elementIsFloat()))
        return false;

    return (true);
  }

  /** instantiates the invariant on the specified slice **/
  protected Invariant instantiate_dyn (PptSlice slice) {
    return (new PairwiseFloatGreaterThan(slice));
  }

  protected PairwiseFloatGreaterThan(PairwiseFloatLessThan swapped_pic) {
    super(swapped_pic.ppt);
    if (logOn())
      log ("Instantiated from resurrect_done_swapped");
  }

  public DiscardInfo isObviousStatically (VarInfo[] vis) {
    VarInfo var1 = vis[0];
    VarInfo var2 = vis[1];

    Object[] obv1 = SubSequenceFloat.isObviousSubSequence(var1, var2);
    Object[] obv2 = SubSequenceFloat.isObviousSubSequence(var2, var1);
    if (obv1[1] != null) {
      Global.implied_noninstantiated_invariants++;
      return new DiscardInfo(this, (DiscardCode) obv1[0], (String) obv1[1]);
    } else if (obv2[1] != null) {
      Global.implied_noninstantiated_invariants++;
      return new DiscardInfo(this, (DiscardCode) obv1[0], (String) obv1[1]);
    }

    // Don't instantiate if the variables can't have order
    if (!var1.aux.getFlag(VarInfoAux.HAS_ORDER) ||
        !var2.aux.getFlag(VarInfoAux.HAS_ORDER)) {
      if (debug.isLoggable(Level.FINE)) {
        debug.fine ("Not instantitating for because order has no meaning: " +
                     var1.name() + " and " + var2.name());
      }
      return new DiscardInfo(this, DiscardCode.obvious, "Obvious statically since order has no meaning");
    }

    return super.isObviousStatically (vis);
  }

  public DiscardInfo isObviousDynamically (VarInfo[] vis) {
    DiscardInfo super_result = super.isObviousDynamically(vis);
    if (super_result != null) {
      return super_result;
    }

    // Subsequence invariants are implied by the same invariant over
    // the supersequence
    DiscardInfo di = superseq_implies (vis);
    if (di != null)
      return (di);

    return null;
    }

  /**
   * Checks to see if the same invariant exists over supersequences of
   * these variables:
   *
   *    (A[] op B[]) ^ (i == j)  ==> A[i..] op B[j..]
   *    (A[] op B[]) ^ (i == j)  ==> A[..i] op B[..j]
   */
  private DiscardInfo superseq_implies (VarInfo[] vis) {

    // Make sure the variables are SequenceFloatSubsequence with the same start/end
    VarInfo v1 = vis[0];
    VarInfo v2 = vis[1];
    if (!v1.isDerived() || !(v1.derived instanceof SequenceFloatSubsequence))
      return (null);
    if (!v2.isDerived() || !(v2.derived instanceof SequenceFloatSubsequence))
      return (null);
    SequenceFloatSubsequence der1 = (SequenceFloatSubsequence) v1.derived;
    SequenceFloatSubsequence der2 = (SequenceFloatSubsequence) v2.derived;
    if ((der1.from_start != der2.from_start)
        || (der1.index_shift != der2.index_shift))
      return (null);

    // Make sure the subscripts are equal
    DiscardInfo di = new DiscardInfo (this, DiscardCode.obvious, "");
    if (!ppt.parent.check_implied_canonical (di, der1.sclvar(), der2.sclvar(),
                                             IntEqual.get_proto()))
      return (null);

    // See if the super-sequences have the same invariant
    if (!ppt.parent.check_implied_canonical (di, der1.seqvar(), der2.seqvar(),
                                             PairwiseFloatGreaterThan.get_proto()))
      return (null);

    // Add in the vis variables to di reason (if they are different)
    di.add_implied_vis (vis);
    return (di);
  }

  protected Invariant resurrect_done_swapped() {

      return new PairwiseFloatLessThan(this);
  }

  /**
   * Returns the class that corresponds to this class with its variable
   * order swapped.
   */
  public static Class swap_class () {
    return PairwiseFloatLessThan.class;
  }

  public String repr() {
    return "PairwiseFloatGreaterThan" + varNames() + ": ";
  }

  public String getComparator() {
    return ">";
  }

  public String format_using(OutputFormat format) {

    if (format.isJavaFamily()) return format_java_family(format);

    if (format == OutputFormat.DAIKON) return format_daikon();
    if (format == OutputFormat.IOA) return format_ioa();
    if (format == OutputFormat.ESCJAVA) return format_esc();
    if (format == OutputFormat.SIMPLIFY) return format_simplify();

    return format_unimplemented(format);
  }

  public String format_daikon() {
    return var1().name() + " > " + var2().name()
      + " (elementwise)";
  }

  /* IOA */
  public String format_ioa() {
    if (var1().isIOASet() || var2().isIOASet())
      return "Not valid for sets: " + format();
    Quantify.IOAQuantification quant1 = VarInfo.get_ioa_quantify (var1());
    Quantify.IOAQuantification quant2 = VarInfo.get_ioa_quantify (var2());

    return quant1.getQuantifierExp() + quant1.getVarIndexedString(0) + " " +
      "> " + quant2.getVarIndexedString(0) + quant1.getClosingExp();
  }

  public String format_esc() {
    String[] form = VarInfo.esc_quantify (var1(), var2());
    return form[0] + "(" + form[1] + " > " + form[2] + ")" + form[3];
  }

  public String format_simplify() {
    String[] form = VarInfo.simplify_quantify (QuantFlags.element_wise(),
                                               var1(), var2());
    return form[0] + "(> " + form[1] + " " + form[2] + ")" + form[3];
  }

  public String format_java_family(OutputFormat format) {
    return "daikon.Quant.pairwiseGT(" + var1().name_using(format)
      + ", " + var2().name_using(format) + ")";
  }

  public InvariantStatus check_modified(double[] a1, double[] a2, int count) {
    assert a1 != null && a2 != null
      : var1() + " " + var2() + FileIO.data_trace_state.reader.getLineNumber();
    if (a1.length != a2.length || a1.length == 0 || a2.length == 0) {
      // destroyAndFlow();
      return InvariantStatus.FALSIFIED;
    }

    int len = a1.length;
    // int len = Math.min(a1.length, a2.length);

    for (int i=0; i<len; i++) {
      double v1 = a1[i];
      double v2 = a2[i];
      if (! (Global.fuzzy.gt (v1, v2)) ) {
        //  destroyAndFlow();
        return InvariantStatus.FALSIFIED;
      }
    }
    return InvariantStatus.NO_CHANGE;
  }

    public InvariantStatus add_modified(double[] a1, double[] a2,
                                        int count) {
      if (logDetail())
        log (debug, "saw add_modified (" + ArraysMDE.toString(a1) +
             ", " + ArraysMDE.toString(a2) + ")");
      return check_modified(a1, a2, count);
    }

  protected double computeConfidence() {
    // num_elt_values() would be more appropriate
    // int num_values = ((PptSlice2) ppt).num_elt_values();
    int num_values = ppt.num_samples();
    if (num_values == 0) {
      return Invariant.CONFIDENCE_UNJUSTIFIED;
    } else {

      return 1 - Math.pow(.5, num_values);
    }
  }

  public boolean isSameFormula(Invariant other) {
    return true;
  }

  public boolean isExclusiveFormula(Invariant other) {
    return false;
  }

  // Look up a previously instantiated invariant.
  public static PairwiseFloatGreaterThan find(PptSlice ppt) {
    Assert.assertTrue(ppt.arity() == 2);
    for (Invariant inv : ppt.invs) {
      if (inv instanceof PairwiseFloatGreaterThan)
        return (PairwiseFloatGreaterThan) inv;
    }
    return null;
  }

  /**
   * Returns a list of non-instantiating suppressions for this invariant.
   */
  public NISuppressionSet get_ni_suppressions() {
    return (suppressions);
  }

  /** Definition of this invariant (the suppressee) **/
  private static NISuppressee suppressee
    = new NISuppressee (PairwiseFloatGreaterThan.class, 2);

  // Suppressor definitions (used in suppressions below)
  private static NISuppressor
    v1_eq_v2 = new NISuppressor (0, 1, PairwiseFloatEqual.class),
    v1_gt_v2 = new NISuppressor (0, 1, PairwiseFloatGreaterThan.class),
    v1_lt_v2 = new NISuppressor (0, 1, PairwiseFloatLessThan.class);

    private static NISuppressionSet suppressions = null;

}
