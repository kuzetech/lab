function request()
   local str1 = 'This is a string.'
   return wrk.format("POST", "/test", {}, str1)
end