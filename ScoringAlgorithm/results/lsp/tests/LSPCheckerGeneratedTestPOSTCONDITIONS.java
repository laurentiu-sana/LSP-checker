package lsp.tests;

/** Test case generated by the LSP checker; feel free to hack it! */
@SuppressWarnings({"unchecked", "deprecation"})
public class LSPCheckerGeneratedTestPOSTCONDITIONS
{
    public static void main(String[] args)
    {
        try { new lsp.test.postconditions.DummyPostcondition(); } catch(Exception e) { }
        //try { new lsp.test.postconditions.DummyPostcondition().postcond1(0); } catch(Exception e) { }
        //try { new lsp.test.postconditions.DummyPostcondition().postcond2(0,0); } catch(Exception e) { }
        //try { new lsp.test.postconditions.DummyPostcondition().postcond3(0,0); } catch(Exception e) { }
        //try { new lsp.test.postconditions.DummyPostcondition().postcond4(0,0); } catch(Exception e) { }
        //try { new lsp.test.postconditions.DummyPostcondition().postcond5(0,0); } catch(Exception e) { }
        //try { new lsp.test.postconditions.DummyPostcondition().postcond6(0); } catch(Exception e) { }
        //try { new lsp.test.postconditions.DummyPostcondition().postcond7(0); } catch(Exception e) { }
        //try { new lsp.test.postconditions.DummyPostcondition().postcond8(0); } catch(Exception e) { }
        
        /** Invoke interface */
        try { new lsp.test.postconditions.DummyPostcondition().postcond9(new lsp.test.postconditions.SuperClassPostconditions(),0); } catch(Exception e) { }
        try { new lsp.test.postconditions.DummyPostcondition().postcond9(new lsp.test.postconditions.SubClassPostconditions(),0); } catch(Exception e) { }
        try { new lsp.test.postconditions.DummyPostcondition().postcond9(new lsp.test.postconditions.NothingClassPostconditions(),0); } catch(Exception e) { }
        
        /** Invoke virtual */
        try { new lsp.test.postconditions.DummyPostcondition().postcond10(new lsp.test.postconditions.SubClassPostconditions(),0); } catch(Exception e) { }
        try { new lsp.test.postconditions.DummyPostcondition().postcond10(new lsp.test.postconditions.NothingClassPostconditions(),0); } catch(Exception e) { }
    }
}
