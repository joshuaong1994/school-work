#!/bin/bash

clear

# Declare Global Variables
# Make indexed array and 
# also integer attributes
declare -a TITLE
declare -a AUTHOR

declare -a PRICE
declare -a -i AVAIL
declare -a -i SOLD

declare -a TOTAL_SALES

declare -i COUNT=0

declare -a num_books_found
declare -a exist
declare -a index

declare FILENAME="./BookDB.txt"

function press_enter
{
    echo ""
    echo -n "Press Enter to continue"
    read
    clear
}

# =================================================================
#                       MENU OPTIONS code
# =================================================================

function add_new_book
{
    echo "-----------------------------------------"
    echo "<ADD NEW BOOK>"
    echo ""

    exist=true;
    
    while $exist; do
	exist=false;
	empty=true;
	
	# Check if title is being inputed
	while $empty; do
	    empty=false;
	    read -p "Title:  " get_title
	    if test "$get_title" == ""; then
		empty=true;
	        tput setf 4;echo "Please Put in a Title";tput setf 2;
		echo ""
	    fi
	done

	empty=true;
	
	# Check if author is being inputed
	while $empty; do
	    empty=false
	    read -p "Author: " get_author
	    if test "$get_author" == ""; then
		empty=true;
	        tput setf 4;echo "Please Put in an Author";tput setf 2;
		echo ""
	    fi
	done

	# Once both fields are already filled up
	# Check if this book by this author exist
	does_book_exists "$get_title" "$get_author"

	if $exist; then
	    tput setf 4;echo "Book Already Exist!";tput setf 2;
	    echo ""
	fi
    done

    # Put in the title and author
    # into the array
    TITLE[$COUNT]=$get_title;
    AUTHOR[$COUNT]=$get_author;

    empty=true

    # Get price from user and
    # check against the price
    while $empty; do
    	empty=false;
	read -p "Price:  " get_price
	
	# the code commented out is to check if input contains letters
	#if ! [[ $get_price =~ ^[0-9]+(\.[0-9]+)?$ ]]; then
	#    empty=true;
	#    tput setf 4;echo "Please Put in a Price";tput setf 2;  
	awk 'BEGIN{if ('$get_price'>'0') exit 1}'
	if [ $? -eq 1 ]; then
	    PRICE[$COUNT]=$get_price

	else
	    empty=true;
	    tput setf 4;echo "Please Put in a Price";tput setf 2;   
	fi
    done
    
    empty=true

    # Get Quantity Available
    # check against the quantity avail
    while $empty; do
    	empty=false;
	read -p "Quantity Avail:  " get_avail
    	
	if [[ $get_avail =~ ^[\-0-9]+$ ]] && (($get_avail > 0)); then
	    AVAIL[$COUNT]=$get_avail

	else
	    empty=true;
	    tput setf 4;echo "Please Put in a Quantity Available";tput setf 2;
	fi
    done

    empty=true

    # Get Quantity Sold
    # check against the quantity sold
    while $empty; do
    	empty=false;
	read -p "Quantity Sold:   " get_sold
    	
	if [[ $get_sold =~ ^[\-0-9]+$ ]] && (($get_sold > 0)); then
	    SOLD[$COUNT]=$get_sold

	else
	    empty=true;
	    tput setf 4;echo "Please Put in a Quantity Sold";tput setf 2; 
	fi
    done
    
    let COUNT++;

    update_bookdb_data;

    echo "New Book Title '"$get_title"' has been added successfully!"
    echo "-----------------------------------------"
}

function remove_existing_book
{
    echo "-----------------------------------------"
    echo "<REMOVE EXISTING BOOK>"
    echo ""

    exist=false;
    empty=true;
    
    # Check if input Title is empty
    while $empty; do
    	empty=false;
    	read -p "Title:  " get_title
	if test "$get_title" == ""; then
	    empty=true;
	    tput setf 4;echo "Please input a Title";tput setf 2;
    	    echo ""
    	fi
    done
    
    empty=true;
    
    # Check if input Author is empty
    while $empty; do
        empty=false;
	read -p "Author: " get_author
    	if test "$get_author" == ""; then
	    empty=true;
	    tput setf 4;echo "Please input an Author";tput setf 2;
	    echo ""
	fi
    done

    # get the return value of exist inside the
    # function to check if this book exist
    does_book_exists "$get_title" "$get_author"


    # if exist is true, unset the data
    # and decrease the count of books
    if $exist; then
	unset TITLE[$index]
	unset AUTHOR[$index]
	unset PRICE[$index]
	unset AVAIL[$index]
	unset SOLD[$index]
	
	# overwrite the old data with
	# the new one which includes
	# the unset data
	TITLE=("${TITLE[@]}")
	AUTHOR=("${AUTHOR[@]}")
	PRICE=("${PRICE[@]}")
	AVAIL=("${AVAIL[@]}")
	SOLD=("${SOLD[@]}")

	# Decrease array size
	let COUNT--;

	echo "Book Title '"$get_title"' has been removed successfully!"
	
	update_bookdb_data;

    else
	tput setf 4;echo "Error! Book does not exist!";tput setf 2;
	echo ""
    fi

    echo "-----------------------------------------"
}

function update_book_info
{
    echo "-----------------------------------------"
    echo "<UPDATE BOOK INFO>"
    echo ""

    exist=false;
    empty=true;

    # Check if input for
    # title is empty or not
    while $empty; do
	empty=false;
	read -p "Title:  " get_title
	if test "$get_title" == ""; then
	    empty=true;
	    tput setf 4;echo "Please input a Title";tput setf 2;
	    echo ""
	fi
    done

    empty=true;

    # Check if input for
    # author is empty or not
    while $empty; do
	empty=false;
	read -p "Author: " get_author
	if test "$get_author" == ""; then
	    empty=true;
	    tput setf 4;echo "Please input an Author";tput setf 2;
	    echo ""
	fi
    done

    # get the return value of exist inside the
    # function to check if this book exist
    does_book_exists "$get_title" "$get_author"

    # if book exists, print out sub-menu
    if $exist; then
	echo "  Book found!"
	echo ""
	
	option=''
	until [ "$option" = "f" ]; do
	    exist=false;
	    echo "	a) Update Title"
	    echo "	b) Update Author"
	    echo "	c) Update Price"
	    echo "	d) Update Qty Available"
	    echo "	e) Update Qty Sold"
	    echo "	f) Back to Main Menu"
	    echo ""
	    read -p "Option: " option

	    echo "-----------------------------------------"

	    case $option in
		[Aa] | [A|a] ) read -p "New Title:  " get_title
		      does_book_exists "$get_title" "${AUTHOR[$index]}"
		      if $exist; then
			  tput setf 4;echo "Error! Book already exists!";tput setf 2;
			  echo "-----------------------------------------"
		      else
		          TITLE[$index]=$get_title
		          echo "The Book Title has been updated successfully!"
			  echo "-----------------------------------------"
		      fi
		      press_enter ;;

		[Bb] | [B|b] ) read -p "New Author: " get_author
		      does_book_exists "${TITLE[$index]}" "$get_author"
		      if $exist; then
			  tput setf 4;echo "Error! Book already exists!";tput setf 2;
			  echo "-----------------------------------------"
		      else
		          AUTHOR[$index]=$get_author
		          echo "The Book Author has been updated successfully!"
			  echo "-----------------------------------------"
		      fi
		      press_enter ;;

		[Cc] | [C|c] ) read -p "New Price:  " get_price
		      awk 'BEGIN{if ('$get_price'>'0') exit 1}'
			  if [ $? -eq 1 ]; then
		          PRICE[$index]=$get_price;
	                  echo "The Book Price has been updated successfully!"
			  echo "-----------------------------------------"
		      else
			  tput setf 4;echo "Please input a valid price!";tput setf 2;
			  echo "-----------------------------------------"
		      fi
		      press_enter ;;

		[Dd] | [D|d] ) read -p "New Qty Available:  " get_avail
		      if [[ $get_avail=~^[\-0-9]+$ ]] && (( $get_avail>0)); then
		          AVAIL[$index]=$get_avail;
		          echo "Book’s Qty Available has been updated successfully!"
			  echo "-----------------------------------------"
		      else
			  tput setf 4;echo "Invalid Qty";tput setf 2;
			  echo "-----------------------------------------"
		      fi
		      press_enter ;;

		[Ee] | [E|e] ) read -p "New Qty Sold:  " get_sold
		      if [[ $get_sold=~^[\-0-9]+$ ]] && (( $get_sold>0)); then
		          SOLD[$index]=$get_sold;
		          echo "Book’s Qty Available has been updated successfully!"
			  echo "-----------------------------------------"
		      else
			  tput setf 4;echo "Invalid Qty";tput setf 2;
			  echo "-----------------------------------------"
		      fi
		      press_enter ;;

		[Ff] | [F|f] ) break ;;

		*   ) tput setf 4;echo "Please select a valid option!";tput setf 2;
		      echo ""
		      press_enter ;;
	    esac
	    clear

	done
    else
	echo "Book not found!"
	echo ""
    fi
    update_bookdb_data;
    echo "-----------------------------------------"
}

function search_book
{
    echo "-----------------------------------------"
    echo "<SEARCH BOOKS>"
    echo ""
    
    records_found=0;
    
    read -p "Title:  " get_title
    read -p "Author: " get_author
    echo ""

    # check if input for both title and author is empty or not
    if test "$get_title" == ""; then
	if test "$get_author" == ""; then
	    records_found=0;
	
	else
	    for ((i=0; $i<${#TITLE[@]}; i++)); do
	    	if echo "${AUTHOR[$i]}" | grep -q -io "$get_author"; then
		    num_books_found[$records_found]="${TITLE[$i]}, ${AUTHOR[$i]}, ${PRICE[$i]}, ${AVAIL[$i]}, ${SOLD[$i]}"
		    let records_found++
	    	fi
	    done
	fi
		
    elif test "$get_author" == ""; then
	for ((i=0; $i<${#TITLE[@]}; i++)); do
	    if echo "${TITLE[$i]}" | grep -q -io "$get_title"; then
		num_books_found[$records_found]="${TITLE[$i]}, ${AUTHOR[$i]}, ${PRICE[$i]}, ${AVAIL[$i]}, ${SOLD[$i]}"
		let records_found++
	    fi
	done
	
    else
	for ((i=0; $i<${#TITLE[@]}; i++)); do
	    if echo "${TITLE[$i]}" | grep -q -io "$get_title"; then
		if echo "${AUTHOR[$i]}" | grep -q -io "$get_author"; then
		    num_books_found[$records_found]="${TITLE[$i]}, ${AUTHOR[$i]}, ${PRICE[$i]}, ${AVAIL[$i]}, ${SOLD[$i]}"
		    let records_found++
		fi
	    fi
	done
    fi

    # If record is found
    if (( $records_found>0 )); then
	echo "Found $records_found records:"
	for ((i=0; $i<$records_found; i++)); do
		echo "${num_books_found[$i]}"
	done

    # If record is NOT found
    else
	if test "$get_title" == ""; then
	    if test "$get_author" == ""; then
	    	tput setf 4;echo "Please input at least one field!";tput setf 2;
	    fi
	
	else
	    tput setf 4;echo "Book is not found!";tput setf 2;
	fi
    fi
    
    echo "-----------------------------------------"
}

function process_book_sold
{
    echo "-----------------------------------------"
    echo "<PROCESS BOOK SOLD>"

    exist=false;
    field_empty=true;

    # Check if input for title is empty
    while $field_empty; do
	field_empty=false;
	read -p "Title:  " get_title
	if test "$get_title" == ""; then
	    field_empty=true;
	    tput setf 4;echo "Please input a Title";tput setf 2;
	    echo ""
	fi
    done

    field_empty=true;

    # Check if input for author is empty
    while $field_empty; do
	field_empty=false;
	read -p "Author: " get_author
	if test "$get_author" == ""; then
	    field_empty=true;
	    tput setf 4;echo "Please input an Author";tput setf 2;
	    echo ""
	fi
    done

    # Once both fields are already filled up
    # Check if this book by this author exist
    does_book_exists "$get_title" "$get_author"

    if $exist; then
	field_empty=true;
	while $field_empty; do
	    field_empty=false;
	    read -p "No. of copies sold: " get_sold
	    leftover=${AVAIL[$index]}-$get_sold
	    echo ""
	    
	    if [[ $get_sold =~ ^[\-0-9]+$ ]] && (( $get_sold>-1 )); then
		if (( $leftover>-1 )); then
		    
		    echo "Current Book Info:"
		    echo "${TITLE[$index]}, ${AUTHOR[$index]}, ${PRICE[$index]}, ${AVAIL[$index]}, ${SOLD[$index]}"

		    # Decrease the number of available books
		    # Increase the number of sold books
		    AVAIL[$index]=${AVAIL[$index]}-$get_sold
		    SOLD[$index]=${SOLD[$index]}+$get_sold

		    echo ""
		    echo "New Book Info:"
		    echo "${TITLE[$index]}, ${AUTHOR[$index]}, ${PRICE[$index]}, ${AVAIL[$index]}, ${SOLD[$index]}"
		
		else
		    field_empty=true;
		    tput setf 4;echo "No. of books sold does not tally. Please check again.";tput setf 2;
		    echo ""
		fi
	    fi
	done

    else
	tput setf 4;echo "Error! Book does not exist!";tput setf 2;
	echo ""
    fi

    update_bookdb_data;

    echo "-----------------------------------------"
}

function inventory_summary_report
{
    echo "-----------------------------------------"
    echo "<INVENTORY SUMMARY REPORT>"
    echo ""
    
    printf "%-35s\t%-15s\t%-10s\t%-5s\t%-5s\t%-10s\n" "Title" "Author" "Price" "Qty Avail." "Qty Sold" "Total Sales"
    echo ""

    # Print out all the data from database
    for ((i=0; $i<${#TITLE[@]}; i++)); do
	printf "%-35s\t%-15s\t$%-0.2f\t\t%-3d\t\t%-3d\t\t$%-0.2f\n" "${TITLE[$i]}" "${AUTHOR[$i]}" "${PRICE[$i]}" "${AVAIL[$i]}" "${SOLD[$i]}" "$(echo ${PRICE[$i]}*${SOLD[$i]} | bc)"
    done

    echo "-----------------------------------------"
}

function report_books_statistics
{
    echo "-----------------------------------------"
    echo "<BOOK STATISTICS REPORT>"
    echo ""

    tempSoldArr=("${SOLD[@]}");
    tempTitleArr=("${TITLE[@]}");
    tempAuthorArr=("${AUTHOR[@]}");
    tempAvailArr=("${AVAIL[@]}");

    numRec=${#tempSoldArr[@]}

    # sort the numbers in descending array
    for ((last=numRec-1; last>0; last--)); do
	for ((i=0; i<last; i++)); do
	    j=$((i+1))
	    if [ ${tempSoldArr[i]} -lt ${tempSoldArr[j]} ]; then
		tempS=${tempSoldArr[$i]}
		tempSoldArr[$i]=${tempSoldArr[$j]}
		tempSoldArr[$j]=$tempS
		
		tempT=${tempTitleArr[$i]}
		tempTitleArr[$i]=${tempTitleArr[$j]}
		tempTitleArr[$j]=$tempT

		tempA=${tempAuthorArr[$i]}
		tempAuthorArr[$i]=${tempAuthorArr[$j]}
		tempAuthorArr[$j]=$tempA

		tempAvail=${tempAvailArr[$i]}
		tempAvailArr[$i]=${tempAvailArr[$j]}
		tempAvailArr[$j]=$tempAvail
	    fi
	done
    done

    # Most popular books
    echo "The Top 5 Sold Books are"
    echo "------------------------"
    printf "%-35s\t%-15s\t%-5s\n" "Title" "Author" "Qty Sold"
    echo ""

    # print from top - down
    for ((i=0; i<5; i++)); do
	printf "%-35s\t%-15s\t%-5s\n" "${tempTitleArr[$i]}" "${tempAuthorArr[$i]}" "${tempSoldArr[i]}"
    done

    echo ""
    echo ""



    # Least popular books
    echo "The Least 5 Sold Books are"
    echo "------------------------"
    printf "%-35s\t%-15s\t%-5s\n" "Title" "Author" "Qty Sold"
    echo ""

    # print from bottom - up
    for ((i=numRec-1; i>numRec-5; i--)); do
	printf "%-35s\t%-15s\t%-5s\n" "${tempTitleArr[$i]}" "${tempAuthorArr[$i]}" "${tempSoldArr[i]}"
    done

    echo ""
    echo ""



    # Books require re-stocking
    echo "Books that require Restocking (20 or Lesser Books left)"
    echo "------------------------"
    printf "%-35s\t%-15s\t%-5s\n" "Title" "Author" "Qty Left"
    echo ""
    
    for ((i=0; i<numRec; i++)); do
    	if [[ ${tempAvailArr[i]} -lt 21 ]]; then
	    printf "%-35s\t%-15s\t%-5s\n" "${tempTitleArr[$i]}" "${tempAuthorArr[$i]}" "${tempAvailArr[i]}"
    	fi
    done
    
    echo "-----------------------------------------"
}

# =================================================================
#                            Add-ons
# =================================================================

# Access data of book database 
function access_bookdb_data
{
    # check existance of bookdb file, 
    # create the file if it does not exist,
    # else continue
    if ! [ -f $FILENAME ] ; then
	touch $FILENAME
    fi

    # Sort the file and replace it
    # so it is easier for reading
    sort $FILENAME -o $FILENAME

    # read the sorted data and store
    # it into the appropriate array
    while IFS=':' read -a LINE; do
        TITLE[$COUNT]=${LINE[0]};
	AUTHOR[$COUNT]=${LINE[1]};
    	PRICE[$COUNT]=${LINE[2]};
	AVAIL[$COUNT]=${LINE[3]};
	SOLD[$COUNT]=${LINE[4]};

	let COUNT++;
    done<$FILENAME
}

function does_book_exists
{
    for ((i=0; $i<${#TITLE[@]}; i++)); do
	if echo "${TITLE[$i]}" | grep -q -io "$1"; then
    	    if echo "${AUTHOR[$i]}" | grep -q -io "$2"; then
    		index=$i
    		exist=true
	    fi
	fi
    done
}

function update_bookdb_data
{
    for ((i=0; $i<${#TITLE[@]}; i++)); do
	echo "${TITLE[$i]}:${AUTHOR[$i]}:${PRICE[$i]}:${AVAIL[$i]}:${SOLD[$i]}"
    done>$FILENAME
}

# =================================================================
#                            MENU code
# =================================================================

# Access the data in Book Database
access_bookdb_data;

#Display menu options and wait for selection
selection=0

until [ "$selection" = "8" ]; do
    echo ""
    echo "Advanced Book Inventory System"
    echo ""
    echo "	1.) Add new book"
    echo "	2.) Remove existing book info"
    echo "	3.) Update book info and quantity"
    echo "	4.) Search for book by title/author"
    echo "	5.) Process a Book Sold"
    echo "	6.) Inventory Summary Report"
    echo "	7.) Book Statistics Report"
    echo "	8.) Quit"
    echo ""
    echo -n "Enter selection: "
    read selection

    # Functions are called in here
    case $selection in
        1 ) add_new_book ; press_enter ;;
        2 ) remove_existing_book ; press_enter ;;
	3 ) update_book_info ; press_enter ;;
	4 ) search_book ; press_enter ;;
	5 ) process_book_sold;press_enter;;
	6 ) inventory_summary_report;press_enter;;
	7 ) report_books_statistics;press_enter;;
        8 ) break ;;
        * ) tput setf 4;echo "Please enter 1, 2, 3, 4, 5, 6, 7 or 8";tput setf 2; press_enter
    esac
done

# Update the Book Database
update_bookdb_data;

