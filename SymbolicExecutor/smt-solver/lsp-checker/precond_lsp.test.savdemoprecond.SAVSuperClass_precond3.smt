(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrasorts ( Ref )
  :extrafuns ( (assertion_disabled Int) )
  :extrafuns (  ( di Ref )  )
  :extrafuns (  ( input.member Int )  )
  :extrafuns (  ( obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation1 Bool )  ( obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation2 Bool )  )
  :extrafuns (  )
  :formula
    (and (or  (and  obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation1  (not obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation2)  )  (and  (not obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation1)  obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation2  ) )  (or  (not (implies (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation2 ( > input.member 10 ))) (not (and  (and ( < input.member 7) (and ( > input.member 5) ( <= input.member 10 ) ) )))) )  (not (implies (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation2 ( > input.member 10 ))) (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation2 ( > input.member 10 )))) )  (not (implies (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation2 ( > input.member 10 ))) (not (and  (and ( < input.member 7) (and ( > input.member 5) ( >= input.member 0 ) ) )))) )  (not (implies (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation2 ( > input.member 10 ))) (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation1 ( < input.member 0 )))) )  (not (implies (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation1 ( < input.member 0 ))) (not (and  (and ( < input.member 7) (and ( > input.member 5) ( <= input.member 10 ) ) )))) )  (not (implies (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation1 ( < input.member 0 ))) (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation2 ( > input.member 10 )))) )  (not (implies (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation1 ( < input.member 0 ))) (not (and  (and ( < input.member 7) (and ( > input.member 5) ( >= input.member 0 ) ) )))) )  (not (implies (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation1 ( < input.member 0 ))) (not (and obj_instanceof_lsp_test_savdemoprecond_MyDummyImplementation1 ( < input.member 0 )))) ) ))
)
