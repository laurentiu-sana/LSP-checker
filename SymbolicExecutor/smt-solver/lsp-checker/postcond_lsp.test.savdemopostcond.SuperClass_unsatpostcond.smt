(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrasorts ( Ref )
  :extrafuns ( (assertion_disabled Int) )
  :extrafuns (  ( x Int )  )
  :extrafuns (  )
  :extrafuns (  ( parameter_instanceof_lsp_test_savdemopostcond_SuperClass Bool )  ( parameter_instanceof_lsp_test_savdemopostcond_SubClass Bool )  )
  :extrafuns (  ( exists Int )  ( + Int )  ( xsub Int )  ( Int Int )  ( xsuper Int )  ( - Int )  )
  :formula
    (and (or  (and  parameter_instanceof_lsp_test_savdemopostcond_SuperClass  (not parameter_instanceof_lsp_test_savdemopostcond_SubClass)  )  (and  (not parameter_instanceof_lsp_test_savdemopostcond_SuperClass)  parameter_instanceof_lsp_test_savdemopostcond_SubClass  ) )  (not (or  (exists ( xsuper Int )  (and  (and parameter_instanceof_lsp_test_savdemopostcond_SubClass ( >= xsuper 0 ) )   (and parameter_instanceof_lsp_test_savdemopostcond_SubClass ( >= xsub 0 ) )   (=  ( + xsuper 1 )   xsub  ) ) ) (exists ( xsuper Int )  (and  (and parameter_instanceof_lsp_test_savdemopostcond_SubClass ( >= xsuper 0 ) )   (and parameter_instanceof_lsp_test_savdemopostcond_SubClass ( < xsub 0 ) )   (=  ( + xsuper 1 )   ( - 0 xsub )  ) ) ) (exists ( xsuper Int )  (and  (and parameter_instanceof_lsp_test_savdemopostcond_SuperClass ( < xsuper 0 ) )   (and parameter_instanceof_lsp_test_savdemopostcond_SubClass ( >= xsub 0 ) )   (=  ( + ( - 0 xsuper ) 1 )   xsub  ) ) ) (exists ( xsuper Int )  (and  (and parameter_instanceof_lsp_test_savdemopostcond_SuperClass ( < xsuper 0 ) )   (and parameter_instanceof_lsp_test_savdemopostcond_SubClass ( < xsub 0 ) )   (=  ( + ( - 0 xsuper ) 1 )   ( - 0 xsub )  ) ) )) ))
)
