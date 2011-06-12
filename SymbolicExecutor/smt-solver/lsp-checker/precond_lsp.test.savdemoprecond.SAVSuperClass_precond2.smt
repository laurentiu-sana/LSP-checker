(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrasorts ( Ref )
  :extrafuns ( (assertion_disabled Int) )
  :extrafuns (  ( y Int )  ( x Int )  )
  :extrafuns (  ( input.member Int )  )
  :extrafuns (  ( obj_instanceof_lsp_test_savdemoprecond_SAVSubClass Bool )  ( obj_instanceof_lsp_test_savdemoprecond_SAVSuperClass Bool )  )
  :extrafuns (  ( + Int )  )
  :formula
    (and (or  (and  obj_instanceof_lsp_test_savdemoprecond_SAVSubClass  (not obj_instanceof_lsp_test_savdemoprecond_SAVSuperClass)  )  (and  (not obj_instanceof_lsp_test_savdemoprecond_SAVSubClass)  obj_instanceof_lsp_test_savdemoprecond_SAVSuperClass  ) )  (or  (not (implies (not (and obj_instanceof_lsp_test_savdemoprecond_SAVSuperClass ( < input.member 5 ))) (not (and  ( > ( + y input.member ) 100 )))) )  (not (implies (not (and  ( < input.member 5 ))) (not (and  ( > ( + y input.member ) 100 )))) )  (not (implies (not (and  (and ( < input.member 10) ( >= input.member 5 ) ))) (not (and  ( > ( + y input.member ) 100 )))) )  (not (implies (not (and obj_instanceof_lsp_test_savdemoprecond_SAVSubClass ( < input.member 10 ))) (not (and  ( > ( + y input.member ) 100 )))) ) ))
)
