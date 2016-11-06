
p = 0;
q = 0;
g = 0;
ky = 0;
kx = 0;

print("\nDigital Signature Algorithm\n");
print("Enter:");
print("\tKeygen(x, y): to produce secret and public key parameters where |P|_2 >= x and |Q|_2 >= y");
print("\tSign(m): to sign a message m using the private key");
print("\tVerify(m, r, s): to verify a message m with signature (r, s)");
print("\tHelp: this screen\n");


Keygen(x, y)={
	
	p = 0;
	q = 0;
	g = 0;
	kx = x;
	ky = y;

	/* pick a prime, such that p-1 has a prime factor q */
	until ((length(binary(p)) >= kx) && (length(binary(q)) >= ky) && ((p-1) % q == 0),
		p = primes(9999)[random(9999)+1]; \
		q = primes(9999)[random(9999)+1]; \
	);
	
	print("|P_2|     = " p " ");
	print("|Q_2|     = " q " ");

	exponent = (p - 1) / q;
	h = random(p);
	
	while(((h^exponent) % p) < 2, h = random(p));
	
	g = ((h^exponent) % p);		/* generator 		*/
	kx = random(q);				/* private key x 	*/
	ky = ((g^kx) % p);			/* public  key y 	*/

	if(q > p, print("Please Generate a new Key again."));
	if(q < p, print("Public Key:\n\tg = " g "\n\ty = " ky "\n\nPrivate Key:\n\tx = " kx "\n"));
}

Sign(m) = {
	until(r > 0 && r < q && s > 0 && s < q,
		k = random(q);				/* choose random k */

		k_inverse = bezout(q, k);	/* find gcd of q, k */
		k_inverse = k_inverse[2];	/* and then get the inverse of k */

		/* if it inverse is a negative number, inverse it again */
		if(k_inverse < 0, k_inverse = k_inverse % q); 

		r = ((g^k) % p) % q;
		s = (k_inverse * (m + kx * r)) % q;
	);

	print("Signature: (" r ", " s ")");
}

Verify(m, r, s) = {
	w = bezout(q, s);
	w = w[2];

	if(w < 0, w = w % q);

	uo = (m * w)%q;
	ut = (r * w)%q;

	v = (g^uo) * (ky^ut);
	v = v % p;
	v = v % q;

	/* signature is accepted, else, signature is NOT accepted */
	if(v == r, print("Verified OK"), print("Reject"));	
}

