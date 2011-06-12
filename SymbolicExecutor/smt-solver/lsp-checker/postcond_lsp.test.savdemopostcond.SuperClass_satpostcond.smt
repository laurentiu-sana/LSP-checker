(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrasorts ( Ref )
  :extrafuns ( (assertion_disabled Int) )
  :extrafuns (  ( x Int )  )
  :extrafuns (  )
  :extrafuns (  ( parameter_instanceof_lsp_test_savdemopostcond_SuperClass Bool )  ( parameter_instanceof_lsp_test_savdemopostcond_SubClass Bool )  )
  :extrafuns (  ( exists Int )  ( xsub Int )  ( + Int )  ( Int Int )  ( xsuper Int )  )
  :formula
    (and (or  (and  parameter_instanceof_lsp_test_savdemopostcond_SuperClass  (not parameter_instanceof_lsp_test_savdemopostcond_SubClass)  )  (and  (not parameter_instanceof_lsp_test_savdemopostcond_SuperClass)  parameter_instanceof_lsp_test_savdemopostcond_SubClass  ) )  (not (or  (exists ( xsuper Int )  (and  (and parameter_instanceof_lsp_test_savdemopostcond_SuperClass )   (and parameter_instanceof_lsp_test_savdemopostcond_SubClass )   (=  xsuper   ( + xsub 10 )  ) ) )) ))
)
